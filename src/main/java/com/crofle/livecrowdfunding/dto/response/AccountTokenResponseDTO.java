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
public class AccountTokenResponseDTO {  // 토큰 재발급 응답
    private Long accountId;
    private String accessToken;
    private String refreshToken;
    private String userEmail;
    private String identificationNumber;
    private Role role;
}