package com.crofle.livecrowdfunding.dto.request;

import com.crofle.livecrowdfunding.domain.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusRequestDTO {
    private Long userId;
    private UserStatus status;
    private boolean deleteReport;     // 신고 내역 삭제 여부
    private Long reportId;            // 삭제할 신고 내역 ID

}
