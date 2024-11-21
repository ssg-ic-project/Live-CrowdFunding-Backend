package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyUserCountResponseDTO {
    private String Month;
    private Long count;
}
