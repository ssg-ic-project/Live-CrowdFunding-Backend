package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.domain.entity.AccountView;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
import com.crofle.livecrowdfunding.dto.request.AccountOAuthRequestDTO;

import java.util.Map;
import java.util.Optional;

public interface AccountService {


    // 일반 로그인
    AccountTokenResponseDTO login(AccountLoginRequestDTO request);

    // 토큰 갱신
    AccountTokenResponseDTO refresh(AccountTokenRequestDTO request);

    // 로그아웃 처리
    void logout(String email);

    // 이메일 찾기
    Optional<AccountView> findEmailByNameAndPhone(AccountFindEmailRequestDTO request);

    // 비밀번호 재설정 이메일 발송
    void sendResetPasswordEmail(AccountPasswordResetRequestDTO request);

    // OAuth 로그인 및 계정 생성
    AccountTokenResponseDTO authenticateOAuthAccount(AccountOAuthRequestDTO request);

    Map<String, Object> getNaverUserInfo(String code, String state);

    // Redis 관련 메서드들
    void saveToken(String email, String accessToken, String refreshToken);

    String getStoredAccessToken(String email);

    String getStoredRefreshToken(String email);

    void deleteTokens(String email);
}
