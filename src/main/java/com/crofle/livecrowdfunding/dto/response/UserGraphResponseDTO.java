package com.crofle.livecrowdfunding.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
//대시보드 신규 가입자 수를 위한 DTO
public class
UserGraphResponseDTO {
    private List<String> labels;
    private List<MonthlyUserCountResponseDTO> users;
    private List<MonthlyUserCountResponseDTO> makers;
    private List<MonthlyUserCountResponseDTO> total;

}
