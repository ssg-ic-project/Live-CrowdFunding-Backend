package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
//맨 위에서 받는 일, 월, 연별 등록 건수, 당일 검토중인 건수, 진행중인 프로젝트 수
public class ProjectStatisticsResponseDTO {
    private Long dailyRegistrations; //일별 등록 건수
    private Long monthlyRegistrations; //월별 등록 건수
    private Long yearlyRegistrations; //연별 등록 건수
    private Long dailyRequests; //당일 검토중인 건수
    private Long ongoingProjects; //진행중인 프로젝트 수

}
