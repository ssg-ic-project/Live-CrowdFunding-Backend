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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@Import(PasswordConfig.class)
public class TotalLoginServiceTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secretKey;

    // 테스트 계정 상수
    private final String TEST_USER_EMAIL = "cccc@ccc.com";
    private final String TEST_MAKER_EMAIL = "bbbb@bbb.com";
    private final String TEST_OAUTH_EMAIL = "oauth@test.com";
    private final String TEST_PASSWORD = "1234"; // cccc@ccc.com의 실제 암호화된 비밀번호

    // 이메일 찾기 테스트용 데이터 (firstsm41@naver.com 계정 정보 사용)
    private final String TEST_NAME = "최성민";
    private final String TEST_PHONE = "010-7529-0837";
    private final String TEST_RESET_EMAIL = "firstsm41@naver.com";


    // 기존 테스트 메소드들
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

        // 네이버 응답 데이터 시뮬레이션
        Map<String, Object> response = new HashMap<>();
        response.put("email", TEST_OAUTH_EMAIL);
        response.put("name", "OAuth테스트");
        response.put("mobile", "010-1234-5678");
        response.put("gender", "M");
        response.put("birthyear", "1990");

        AccountOAuthRequestDTO request = AccountOAuthRequestDTO.fromNaverResponse(response);

        log.info("OAuth 로그인 요청: {}\n", request);
        AccountTokenResponseDTO tokenResponse = accountService.authenticateOAuthAccount(request);

        log.info("토큰 정보:");
        log.info("액세스 토큰: {}", tokenResponse.getAccessToken());
        log.info("리프레시 토큰: {}", tokenResponse.getRefreshToken());
        log.info("이메일: {}", tokenResponse.getUserEmail());
        log.info("역할: {}\n", tokenResponse.getRole());

        assertAll(
                () -> assertNotNull(tokenResponse),
                () -> assertNotNull(tokenResponse.getAccessToken()),
                () -> assertEquals(TEST_OAUTH_EMAIL, tokenResponse.getUserEmail()),
                () -> assertEquals(Role.USER, tokenResponse.getRole())
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
                .email(TEST_RESET_EMAIL)
                .name(TEST_NAME)
                .phone(TEST_PHONE)
                .build();

        log.info("비밀번호 재설정 이메일 발송 요청: {}\n", request);

        assertDoesNotThrow(() -> accountService.sendResetPasswordEmail(request));

        log.info("\n===== 비밀번호 재설정 이메일 발송 테스트 종료 =====\n");
    }

    // 네이버 OAuth 관련 추가 테스트
    @Test
    @DisplayName("네이버 OAuth 전화번호 포맷팅 테스트")
    public void testNaverOAuthPhoneFormatting() {
        log.info("\n\n===== 네이버 OAuth 전화번호 포맷팅 테스트 시작 =====\n");

        // 다양한 형식의 전화번호로 테스트
        Map<String, Object> response1 = createTestResponse("01012345678");
        Map<String, Object> response2 = createTestResponse("010-1234-5678");
        Map<String, Object> response3 = createTestResponse("010 1234 5678");

        AccountOAuthRequestDTO request1 = AccountOAuthRequestDTO.fromNaverResponse(response1);
        AccountOAuthRequestDTO request2 = AccountOAuthRequestDTO.fromNaverResponse(response2);
        AccountOAuthRequestDTO request3 = AccountOAuthRequestDTO.fromNaverResponse(response3);

        log.info("포맷팅 결과:");
        log.info("케이스1: {}", request1.getPhone());
        log.info("케이스2: {}", request2.getPhone());
        log.info("케이스3: {}", request3.getPhone());

        assertAll(
                () -> assertEquals("010-1234-5678", request1.getPhone()),
                () -> assertEquals("010-1234-5678", request2.getPhone()),
                () -> assertEquals("010-1234-5678", request3.getPhone())
        );

        log.info("\n===== 네이버 OAuth 전화번호 포맷팅 테스트 종료 =====\n");
    }

    private Map<String, Object> createTestResponse(String phoneNumber) {
        Map<String, Object> response = new HashMap<>();
        response.put("email", TEST_OAUTH_EMAIL);        // 실제 OAuth 테스트 계정 이메일 사용
        response.put("name", "OAuth테스트");            // 실제 이름 사용
        response.put("mobile", phoneNumber);
        response.put("gender", "M");
        response.put("birthyear", "1990");
        return response;
    }
}
