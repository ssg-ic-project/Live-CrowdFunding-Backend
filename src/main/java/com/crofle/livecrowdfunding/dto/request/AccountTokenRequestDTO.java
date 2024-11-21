package com.crofle.livecrowdfunding.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 토큰 재발급 요청
public class AccountTokenRequestDTO {
    private String accessToken;
    private String refreshToken;
}