package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventLogResponseDTO { //사용자 별 이벤트 당첨 내역 조회
    private String eventName;
    private String winningPrize;
    private String winningDate;
    private String createdAt;
}
