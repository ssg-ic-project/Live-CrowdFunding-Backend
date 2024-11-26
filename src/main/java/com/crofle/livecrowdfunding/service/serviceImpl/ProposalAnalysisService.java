package com.crofle.livecrowdfunding.service.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import com.crofle.livecrowdfunding.dto.request.ProposalRequest;
import com.crofle.livecrowdfunding.dto.response.ProposalAnalysisResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class ProposalAnalysisService {

    private final String claudeApiKey;
    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";

    public ProposalAnalysisService(@Value("${anthropic.api.key}") String claudeApiKey) {
        this.claudeApiKey = claudeApiKey;
    }


    public ProposalAnalysisResponse analyzeProposals(ProposalRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", claudeApiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "claude-3-5-sonnet-20241022");
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.3);
            requestBody.put("system", getSystemPrompt());

            String userMessage = String.format("""
                상품 기획안 1:
                %s
                
                펀딩 기획안 2:
                %s
                """, request.getProjectDocument(), request.getFundingDocument());

            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", userMessage);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(message);
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    CLAUDE_API_URL,
                    HttpMethod.POST,
                    httpRequest,
                    JsonNode.class
            );

            return parseResponse(response.getBody());

        } catch (Exception e) {
            log.error("Claude API 호출 중 오류 발생", e);
            throw new RuntimeException("기획안 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String getSystemPrompt() {
        return """
            당신은 사업 기획안을 평가하는 전문가입니다. 아래 기준으로 두 기획안을 종합하여 평가해주세요:
            
            상품 기획안
	1. 상품 정의(20점)
		- 상품의 핵심 가지와 특장점
		- 목표 고객층 정의
		- 시장 내 경쟁 제품 분석
		- 가격 전략의 적절성
            
            2. 상품 계획(20점)
            		- 생산 방식과 프로세스
		- 생산 용량 및 확장성
		- 품질 관리 방안
		- 원자재/부품 조달 계획
            
            3. 배송 계획 (10점)
		- 배송 및 물류 계획
		- 재고 관리 방안
		- AS/반품 정책
		- 고객 지원 체계

            4. 리스크 관리(10점)
              	- 생산 지연 대응 방안
              	- 품질 이슈 대응 계획
		- 원가 변동 리스크 관리
		- 공급망 리스크 대책

	펀딩 기획안
	1. 펀딩 전략(20점)
		- 펀딩 목표액의 적절성
            		- 상품 홍보 게시글 작성 방향
            		- 상품 홍보 이미지 계획
            		- 할인율 산정
	
	2. 스트리밍 계획(20점)
		- 라이브 방송 컨텐츠 계획
		- 라이브 이벤트 계획
		- 스트리밍 일정 계획

            
            기획안 분석 결과는 100점 만점으로 산출됩니다. 80점 이상이면 통과로 간주합니다.
            통과 시 반려 사유 기재하지 않고, 반려 시 반려 사유를 100자 이내로 적어주세요.
           
            다음 형식으로 JSON으로 응답해주세요:
            {
                "proposalScore": 점수,
                "rejectionReason": "반려 사유"
            }
            """;
    }

    private ProposalAnalysisResponse parseResponse(JsonNode responseBody) {
        try {
            JsonNode content = responseBody.get("content").get(0).get("text");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(content.asText());

            return ProposalAnalysisResponse.builder()
                    .proposalScore(rootNode.get("proposalScore").asInt())
                    .rejectionReason(rootNode.get("rejectionReason").asText())
                    .build();

        } catch (Exception e) {
            log.error("응답 파싱 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("AI 응답 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}