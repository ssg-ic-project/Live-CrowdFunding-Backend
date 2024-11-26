package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.AccountTokenRequestDTO;
import com.crofle.livecrowdfunding.dto.request.AdminAccountLoginRequestDTO;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;

public interface AdminAccountService {
    AccountTokenResponseDTO login(AdminAccountLoginRequestDTO request);
    void saveToken(String identificationNumber, String accessToken, String refreshToken);


    // 토큰 갱신
    AccountTokenResponseDTO refresh(AccountTokenRequestDTO request);
    String getStoredRefreshToken(String employeeNumber);


    // 로그아웃 처리
    void logout(String identificationNumber);
    void deleteTokens(String email);
}
