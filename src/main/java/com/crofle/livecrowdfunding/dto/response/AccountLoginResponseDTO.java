package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 로그인 응답 (토큰 정보 포함)
public class AccountLoginResponseDTO {
    private Long accountId;
    private String accessToken;
    private String refreshToken;
    private String userEmail;
    private Role role;
}