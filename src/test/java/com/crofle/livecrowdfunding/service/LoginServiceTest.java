package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.config.PasswordConfig;
import com.crofle.livecrowdfunding.domain.enums.Role;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
import com.crofle.livecrowdfunding.util.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.*;import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Set;
import java.util.concurrent.TimeUnit;


@SpringBootTest
@Log4j2
@Import(PasswordConfig.class)
public class LoginServiceTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
    @BeforeEach
    void setUp() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            log.info("Redis connection successful");

            Set<String> keys = redisTemplate.keys("password_reset:*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleaned up {} existing test tokens", keys.size());
            }
        } catch (Exception e) {
            log.error("Redis connection failed", e);
            fail("Redis connection failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 발송 및 토큰 저장 테스트")
    public void resetPasswordEmailTest() {
        log.info("\n\n===== 비밀번호 재설정 이메일 발송 테스트 시작 =====\n");

        // Given
        AccountPasswordResetRequestDTO request = AccountPasswordResetRequestDTO.builder()
                .email("firstsm41@naver.com")
                .name("최성민")
                .phone("010-7529-0837")
                .build();

        log.info("비밀번호 재설정 이메일 발송 요청: {}\n", request);

        // When
        accountService.sendResetPasswordEmail(request);
        log.info("이메일 발송 완료");

        // Then
        boolean tokenFound = false;
        Set<String> keys = null;

        for (int i = 0; i < 6; i++) {
            keys = redisTemplate.keys("password_reset:*");
            log.info("Attempt {}: Found {} keys", i + 1, keys != null ? keys.size() : 0);

            if (keys != null && !keys.isEmpty()) {
                tokenFound = true;
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 토큰 저장 확인
        assertTrue(tokenFound, "Redis에 토큰이 저장되지 않았습니다.");
        assertNotNull(keys, "Redis keys가 null입니다.");
        assertFalse(keys.isEmpty(), "Redis에 저장된 토큰이 없습니다.");

        // 저장된 토큰의 첫 번째 키 가져오기
        String storedTokenKey = keys.iterator().next();
        log.info("Found token key: {}", storedTokenKey);

        // 토큰 상태 확인
        String tokenStatus = (String) redisTemplate.opsForValue().get(storedTokenKey);
        assertEquals("unused", tokenStatus, "토큰의 상태가 'unused'가 아닙니다.");
        log.info("Token status verified: {}", tokenStatus);

        // 토큰 만료 시간 확인
        Long ttl = redisTemplate.getExpire(storedTokenKey);
        assertTrue(ttl > 0, "토큰의 만료 시간이 설정되지 않았습니다.");
        log.info("Token TTL: {} seconds", ttl);

        log.info("\n===== 비밀번호 재설정 이메일 발송 테스트 종료 =====\n");
    }

    @Test
    @DisplayName("저장된 재설정 토큰 검증 테스트")
    public void validateStoredResetTokenTest() {
        // Given
        AccountPasswordResetRequestDTO request = AccountPasswordResetRequestDTO.builder()
                .email("firstsm41@naver.com")
                .name("최성민")
                .phone("010-7529-0837")
                .build();

        // 토큰 생성 및 저장
        accountService.sendResetPasswordEmail(request);

        // 저장된 토큰 찾기
        Set<String> keys = redisTemplate.keys("password_reset:*");
        assertTrue(keys != null && !keys.isEmpty(), "토큰이 저장되지 않았습니다.");

        String storedTokenKey = keys.iterator().next();
        String token = storedTokenKey.replace("password_reset:", "");

        // When & Then
        assertTrue(accountService.validateResetToken(token), "토큰 검증에 실패했습니다.");
        assertFalse(accountService.validateResetToken(token), "사용된 토큰이 재사용 가능합니다.");
    }

}
