package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalAnalysisResponse {
    private double proposalScore;
    private String rejectionReason;
}
