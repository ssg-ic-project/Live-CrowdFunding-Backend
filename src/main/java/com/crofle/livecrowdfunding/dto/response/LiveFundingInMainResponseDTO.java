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
    private Long id;
    private String url;
    private String productName;
    private Integer percentage;
    private String classification;
    private Long remainingTime;
}
