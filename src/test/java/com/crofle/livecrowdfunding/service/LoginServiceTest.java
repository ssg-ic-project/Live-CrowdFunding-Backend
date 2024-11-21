package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.config.PasswordConfig;
import com.crofle.livecrowdfunding.domain.enums.Role;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
import com.crofle.livecrowdfunding.util.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.*;import org.springframework.beans.factory.annotation.Value;


@SpringBootTest
@Log4j2
@Import(PasswordConfig.class)
public class LoginServiceTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secretKey;

    private final String TEST_USER_EMAIL = "cccc@ccc.com";
    private final String TEST_MAKER_EMAIL = "bbbb@bbb.com";
    private final String TEST_OAUTH_EMAIL = "oauth@test.com";
    private final String TEST_PASSWORD = "1234";

    @Test
    @DisplayName("일반 사용자 로그인 성공 테스트")
    public void testUserLoginSuccess() {
        log.info("\n\n===== 일반 사용자 로그인 테스트 시작 =====\n");

        AccountLoginRequestDTO request = AccountLoginRequestDTO.builder()
                .email(TEST_USER_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        log.info("로그인 요청: {}\n", request);
        AccountTokenResponseDTO response = accountService.login(request);

        log.info("토큰 정보:");
        log.info("액세스 토큰: {}", response.getAccessToken());
        log.info("리프레시 토큰: {}", response.getRefreshToken());
        log.info("이메일: {}", response.getUserEmail());
        log.info("역할: {}\n", response.getRole());

        assertAll(
                () -> assertNotNull(response),
                () -> assertNotNull(response.getAccessToken()),
                () -> assertEquals(TEST_USER_EMAIL, response.getUserEmail()),
                () -> assertEquals(Role.USER, response.getRole())
        );

        log.info("\n===== 일반 사용자 로그인 테스트 종료 =====\n");
    }

    @Test
    @DisplayName("메이커 로그인 성공 테스트")
    public void testMakerLoginSuccess() {
        log.info("\n\n===== 메이커 로그인 테스트 시작 =====\n");

        AccountLoginRequestDTO request = AccountLoginRequestDTO.builder()
                .email(TEST_MAKER_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        log.info("로그인 요청: {}\n", request);
        AccountTokenResponseDTO response = accountService.login(request);

        log.info("토큰 정보:");
        log.info("액세스 토큰: {}", response.getAccessToken());
        log.info("리프레시 토큰: {}", response.getRefreshToken());
        log.info("이메일: {}", response.getUserEmail());
        log.info("역할: {}\n", response.getRole());

        assertAll(
                () -> assertNotNull(response),
                () -> assertNotNull(response.getAccessToken()),
                () -> assertEquals(TEST_MAKER_EMAIL, response.getUserEmail()),
                () -> assertEquals(Role.MAKER, response.getRole())
        );

        log.info("\n===== 메이커 로그인 테스트 종료 =====\n");
    }

    @Test
    @DisplayName("OAuth 로그인 성공 테스트")
    public void testOAuthLoginSuccess() {
        log.info("\n\n===== OAuth 로그인 테스트 시작 =====\n");

        AccountOAuthRequestDTO request = AccountOAuthRequestDTO.builder()
                .email(TEST_OAUTH_EMAIL)
                .name("OAuth테스트")
                .role(Role.USER)
                .build();

        log.info("OAuth 로그인 요청: {}\n", request);
        AccountTokenResponseDTO response = accountService.authenticateOAuthAccount(request);

        log.info("토큰 정보:");
        log.info("액세스 토큰: {}", response.getAccessToken());
        log.info("리프레시 토큰: {}", response.getRefreshToken());
        log.info("이메일: {}", response.getUserEmail());
        log.info("역할: {}\n", response.getRole());

        assertAll(
                () -> assertNotNull(response),
                () -> assertNotNull(response.getAccessToken()),
                () -> assertEquals(TEST_OAUTH_EMAIL, response.getUserEmail()),
                () -> assertEquals(Role.USER, response.getRole())
        );

        log.info("\n===== OAuth 로그인 테스트 종료 =====\n");
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    public void testLoginFailureWrongPassword() {
        log.info("\n\n===== 잘못된 비밀번호 테스트 시작 =====\n");

        AccountLoginRequestDTO request = AccountLoginRequestDTO.builder()
                .email(TEST_USER_EMAIL)
                .password("wrongpassword")
                .build();

        log.info("로그인 요청: {}\n", request);

        assertThrows(IllegalArgumentException.class,
                () -> accountService.login(request),
                "잘못된 비밀번호로 로그인 시 예외가 발생해야 합니다"
        );

        log.info("\n===== 잘못된 비밀번호 테스트 종료 =====\n");
    }

    @Test
    @DisplayName("토큰 갱신 테스트")
    public void testTokenRefresh() throws InterruptedException {
        log.info("\n\n===== 토큰 갱신 테스트 시작 =====\n");

        // 먼저 로그인
        AccountLoginRequestDTO loginRequest = AccountLoginRequestDTO.builder()
                .email(TEST_USER_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        AccountTokenResponseDTO loginResponse = accountService.login(loginRequest);

        // 토큰 생성 시간에 차이를 주기 위해 잠시 대기
        Thread.sleep(1000);  // 1초 대기

        // 토큰 갱신
        AccountTokenRequestDTO refreshRequest = AccountTokenRequestDTO.builder()
                .accessToken(loginResponse.getAccessToken())
                .refreshToken(loginResponse.getRefreshToken())
                .build();

        log.info("토큰 갱신 요청: {}\n", refreshRequest);

        AccountTokenResponseDTO response = accountService.refresh(refreshRequest);

        log.info("갱신된 토큰:");
        log.info("새 액세스 토큰: {}", response.getAccessToken());
        log.info("새 리프레시 토큰: {}", response.getRefreshToken());
        log.info("이메일: {}", response.getUserEmail());
        log.info("역할: {}\n", response.getRole());

        assertAll(
                () -> assertNotNull(response),
                () -> assertNotNull(response.getAccessToken()),
                () -> assertNotEquals(loginResponse.getAccessToken(), response.getAccessToken()),
                () -> assertNotEquals(loginResponse.getRefreshToken(), response.getRefreshToken()),
                () -> assertEquals(TEST_USER_EMAIL, response.getUserEmail()),
                () -> assertTrue(jwtUtil.validateToken(response.getAccessToken())),
                () -> assertTrue(jwtUtil.validateToken(response.getRefreshToken()))
        );

        log.info("\n===== 토큰 갱신 테스트 종료 =====\n");
    }

    @Test
    @DisplayName("이메일 발송 테스트")
    public void resetPasswordTest() {
        log.info("\n\n===== 비밀번호 재설정 이메일 발송 테스트 시작 =====\n");

        AccountPasswordResetRequestDTO request = AccountPasswordResetRequestDTO.builder()
                .email("firstsm41@naver.com")
                .name("최성민")
                .phone("010-7529-0837")
                .build();

        log.info("비밀번호 재설정 이메일 발송 요청: {}\n", request);

        assertDoesNotThrow(() -> accountService.sendResetPasswordEmail(request));

        log.info("\n===== 비밀번호 재설정 이메일 발송 테스트 종료 =====\n");
    }



}