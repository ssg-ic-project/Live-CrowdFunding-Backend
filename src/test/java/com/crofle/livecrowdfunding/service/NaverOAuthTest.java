package com.crofle.livecrowdfunding.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Log4j2
public class NaverOAuthTest {

    private static final String CLIENT_ID = "Rb3vhJIp1oI1q3_oAJiB";
    private static final String CLIENT_SECRET = "0qFxt5jE3g";
    private static final String REDIRECT_URI = "http://localhost:8080/login/oauth2/code/naver";
    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));

            // 1. 인증 URL 생성
            String state = "randomstate123";
            String authUrl = createAuthorizationUrl(state);

            // 2. 사용자 안내
            printMessage("\n==============================================");
            printMessage("1. 아래 URL을 브라우저에 복사");
            printMessage(authUrl);
            printMessage("\n3. 리다이렉트된 URL의 'code' 파라미터 값 복사");
            printMessage("예: http://localhost:8080/login/oauth2/code/naver?code=ABCD1234&state=randomstate123");
            printMessage("\n4. 복사한 코드값 입력");
            printMessage("==============================================\n");

            // 3. 사용자 입력 받기
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            System.out.print("인증 코드를 입력하세요: ");
            String code = reader.readLine();

            if (code != null && !code.trim().isEmpty()) {

                // 4. 토큰 요청 파라미터 로그 출력
                log.info("토큰 요청 파라미터:");
                log.info("client_id: {}", CLIENT_ID);
                log.info("client_secret: {}", CLIENT_SECRET);
                log.info("code: {}", code);
                log.info("state: {}", state);
                log.info("redirect_uri: {}", REDIRECT_URI);

                // 5. 액세스 토큰 요청
                ResponseEntity<Map> tokenResponse = requestAccessToken(code, state);

                if (tokenResponse.getStatusCode() == HttpStatus.OK && tokenResponse.getBody() != null) {
                    log.info("토큰 응답: {}", tokenResponse.getBody());

                    if (tokenResponse.getBody().containsKey("access_token")) {
                        String accessToken = (String) tokenResponse.getBody().get("access_token");
                        log.info("액세스 토큰: {}", accessToken);

                        // 6. 사용자 정보 요청
                        ResponseEntity<Map> userInfoResponse = getUserInfo(accessToken);

                        if (userInfoResponse.getStatusCode() == HttpStatus.OK && userInfoResponse.getBody() != null) {
                            Map<String, Object> responseData = (Map<String, Object>) userInfoResponse.getBody().get("response");

                            if (responseData != null) {
                                printMessage("\n==============================================");
                                printMessage("네이버 로그인 성공!");
                                printMessage("이메일: " + responseData.get("email"));
                                printMessage("이름: " + responseData.get("name"));
                                printMessage("==============================================\n");

                                // 전체 응답 데이터 로깅
                                log.info("사용자 정보 전체 응답: {}", userInfoResponse.getBody());
                            } else {
                                log.error("사용자 정보 응답에 'response' 데이터가 없습니다.");
                            }
                        } else {
                            log.error("사용자 정보 요청 실패: {}", userInfoResponse.getStatusCode());
                        }
                    } else {
                        log.error("토큰 응답에 access_token이 없습니다.");
                    }
                } else {
                    log.error("토큰 요청 실패: {}", tokenResponse.getStatusCode());
                    log.error("에러 응답: {}", tokenResponse.getBody());
                }
            } else {
                log.error("인증 코드가 비어있습니다.");
            }

        } catch (Exception e) {
            log.error("오류 발생: ", e);
        }


    }

    private static void printMessage(String message) {
        System.out.println(message);
    }

    private static String createAuthorizationUrl(String state) {
        return UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    private static ResponseEntity<Map> requestAccessToken(String code, String state) {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("code", code);
        params.add("state", state);
        params.add("redirect_uri", REDIRECT_URI);  // redirect_uri 추가

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAcceptCharset(java.util.Collections.singletonList(StandardCharsets.UTF_8));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return restTemplate.postForEntity(tokenUrl, request, Map.class);
    }

    private static ResponseEntity<Map> getUserInfo(String accessToken) {
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAcceptCharset(java.util.Collections.singletonList(StandardCharsets.UTF_8));

        HttpEntity<?> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                Map.class
        );
    }
}