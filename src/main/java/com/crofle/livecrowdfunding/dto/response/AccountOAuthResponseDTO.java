package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.enums.Role;

public class AccountOAuthResponseDTO {  // OAuth 로그인 응답
    private String accessToken;
    private String refreshToken;
    private String userEmail;
    private Role role;
}