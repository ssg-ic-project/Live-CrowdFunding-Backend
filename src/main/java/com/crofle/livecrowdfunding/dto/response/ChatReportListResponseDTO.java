package com.crofle.livecrowdfunding.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
//관리자 신고 현황 조회 시 ChatReportList 보여주기
public class ChatReportListResponseDTO {
//    private Long id;
//    private ProjectResponseInfoDTO project;
//    private UserInfoResponseDTO user;
//    private String chatMessage;
//    private LocalDateTime createdAt;

    private Long id;
    private Long userId;      // User의 id만
    private Long projectId;   // Project의 id만
    private Long managerId;   // Manager의 id만
    private String reason;
    private String chatMessage;
    private LocalDateTime createdAt;
}
