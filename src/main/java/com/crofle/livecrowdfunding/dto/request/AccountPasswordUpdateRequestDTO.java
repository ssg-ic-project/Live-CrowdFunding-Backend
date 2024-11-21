package com.crofle.livecrowdfunding.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 새 비밀번호 업데이트 요청
public class AccountPasswordUpdateRequestDTO {
    private String email;
    private String newPassword;
    private String token;
}
