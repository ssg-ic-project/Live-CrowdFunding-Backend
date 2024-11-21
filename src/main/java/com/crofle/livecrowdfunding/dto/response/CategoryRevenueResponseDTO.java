package com.crofle.livecrowdfunding.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class CategoryRevenueResponseDTO {
    private List<String> labels;
    private List<CategoryStatsResponseDTO> catrevenue;

}
