package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.request.ProposalRequest;
import com.crofle.livecrowdfunding.dto.response.ProposalAnalysisResponse;
import com.crofle.livecrowdfunding.service.serviceImpl.ProposalAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalAnalysisController {

    private final ProposalAnalysisService analysisService;

    @PostMapping("/analyze")
    public ResponseEntity<ProposalAnalysisResponse> analyzeProposals(
            @RequestBody ProposalRequest request) {
        ProposalAnalysisResponse analysis = analysisService.analyzeProposals(request);
        return ResponseEntity.ok(analysis);
    }
}
