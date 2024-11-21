package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveFundingInMainResponseDTO {
    private String url;
    private String productName;
    private Integer percentage;
   // private Integer remainingTime;
    private String makerName;
    private String classification;
    private Long remainingTime;
}
