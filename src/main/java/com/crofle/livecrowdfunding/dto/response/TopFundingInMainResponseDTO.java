package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopFundingInMainResponseDTO {
    private Integer ranking;
    private String url;
    private String productName;
    private Integer percentage;
    private Integer remainingTime;
    private String makerName;
    private Integer price;
}
