package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.domain.entity.AccountView;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
import com.crofle.livecrowdfunding.dto.request.AccountOAuthRequestDTO;
import com.crofle.livecrowdfunding.dto.response.BusinessVerificationResponseDTO;
import com.crofle.livecrowdfunding.dto.response.EmploymentCertOcrResponseDTO;
import com.crofle.livecrowdfunding.dto.response.IdCardOcrResponseDTO;
import org.springframework.web.multipart.MultipartFile;

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

    // 비밀번호 재설정 페이지 접근 전 토큰 검증
    boolean validateResetToken(String token);

    // 실제 비밀번호 재설정 처리
    void resetPassword(String token, String email, String newPassword);

    // OAuth 로그인 및 계정 생성
    AccountTokenResponseDTO authenticateOAuthAccount(AccountOAuthRequestDTO request);

    Map<String, Object> getNaverUserInfo(String code, String state);

    // Redis 관련 메서드들
    void saveToken(String email, String accessToken, String refreshToken);

    String getStoredAccessToken(String email);

    String getStoredRefreshToken(String email);

    void deleteTokens(String email);

    // 인증 코드 생성 및 발송 메서드 추가
    String sendVerificationEmail(String email);

    // 인증 코드 생성 메서드
    String generateVerificationCode();

    // 인증 코드 확인 메서드
    boolean verifyCode(String email, String code);

    boolean isEmailDuplicate(String email);

    IdCardOcrResponseDTO verifyIdCard(MultipartFile idCard);
    EmploymentCertOcrResponseDTO verifyEmploymentCert(MultipartFile employmentCert);
    BusinessVerificationResponseDTO verifyBusinessNumber(String businessNumber);

}
