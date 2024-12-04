package com.crofle.livecrowdfunding.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FundingStatusResponseDTO {
    private int success;
    private int failed;
}