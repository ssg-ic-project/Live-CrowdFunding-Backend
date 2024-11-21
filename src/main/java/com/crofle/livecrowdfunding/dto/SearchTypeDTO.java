package com.crofle.livecrowdfunding.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchTypeDTO {
    //Project 관리용
    private String RS;   // 검토 상태 review status
    private String PS;   // 진행 상태 progress status
    private String SD;  // start date
    private String ED;  // end date

    //User 관리용
    private String US; //user status 회원 상태
    private String MT; //회원 유형
}
