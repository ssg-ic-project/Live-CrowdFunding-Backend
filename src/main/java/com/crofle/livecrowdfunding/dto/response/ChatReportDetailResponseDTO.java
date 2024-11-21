package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
//관리자쪽 신고현황에서 모달창으로 상세조회하기
public class ChatReportDetailResponseDTO {
    private Long id;
    private Long projectId;
    private String projectName;
    private Long userId;
    private UserStatus userStatus; //Enum.USERSTATUS?
    private String chatMessage;
    private LocalDateTime createdAt; //신고 날짜
    private Long managerId; //신고 직원ID
    private String reason; //관리자 코멘트

}
