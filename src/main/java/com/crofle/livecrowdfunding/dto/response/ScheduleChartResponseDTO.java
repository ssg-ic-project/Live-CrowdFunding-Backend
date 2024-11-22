package com.crofle.livecrowdfunding.dto.response;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleChartResponseDTO {
    private Long projectId;
    private String date;
    private String time;
    private String productName;
    private String makerName;
    private Integer percentage;
    private Integer originalPrice;
    private Integer discountedPrice;
    private String thumbnailUrl;
}
