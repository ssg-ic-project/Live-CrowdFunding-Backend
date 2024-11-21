package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.User;
import com.crofle.livecrowdfunding.domain.enums.UserStatus;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.repository.UserRepository;
import com.crofle.livecrowdfunding.util.JwtUtil;
import com.crofle.livecrowdfunding.domain.entity.AccountView;
import com.crofle.livecrowdfunding.domain.enums.Role;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
import com.crofle.livecrowdfunding.repository.redis.AccountViewRepository;
import com.crofle.livecrowdfunding.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountViewRepository accountViewRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirectUri;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;    // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일


    @Override
    public AccountTokenResponseDTO login(AccountLoginRequestDTO request) {
        AccountView account = accountViewRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        log.info("사용된 PasswordEncoder 클래스: {}", passwordEncoder.getClass().getName());
        log.info("Found account: {}", account);
        log.info("Input password: {}, Stored password: {}", request.getPassword(), account.getPassword());

        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), account.getPassword());
        log.info("Password match result: {}", passwordMatch);

        if (!passwordMatch) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String accessToken = jwtUtil.createAccessToken(account.getEmail(), account.getRole());
        String refreshToken = jwtUtil.createRefreshToken(account.getEmail(), account.getRole());

        // Redis에 토큰 저장
        saveToken(account.getEmail(), accessToken, refreshToken);

        return AccountTokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userEmail(account.getEmail())
                .role(account.getRole())
                .build();
    }

    @Override
    public AccountTokenResponseDTO refresh(AccountTokenRequestDTO request) {
        // 1. 리프레시 토큰 검증
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // 2. Redis에서 저장된 토큰 확인
        String email = jwtUtil.getEmailFromToken(request.getRefreshToken());
        String storedRefreshToken = getStoredRefreshToken(email);

        if (!request.getRefreshToken().equals(storedRefreshToken)) {
            throw new IllegalArgumentException("Refresh token not found");
        }

        Role role = jwtUtil.getRoleFromToken(request.getRefreshToken());

        // 3. 실제 사용자가 존재하는지 확인
        AccountView account = accountViewRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 4. 새로운 토큰 생성
        String newAccessToken = jwtUtil.createAccessToken(account.getEmail(), account.getRole());
        String newRefreshToken = jwtUtil.createRefreshToken(account.getEmail(), account.getRole());

        // 5. Redis에 새로운 토큰 저장
        saveToken(email, newAccessToken, newRefreshToken);

        return AccountTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userEmail(account.getEmail())
                .role(account.getRole())
                .build();
    }

    @Override
    public void logout(String email) {
        // Redis에서 토큰 삭제
        deleteTokens(email);
        log.info("User logged out and tokens deleted: {}", email);
    }

    @Override
    public Optional<AccountView> findEmailByNameAndPhone(AccountFindEmailRequestDTO request) {
        return accountViewRepository.findEmailByNameAndPhone(request.getName(), request.getPhone());
    }

    @Override
    public void sendResetPasswordEmail(AccountPasswordResetRequestDTO request) {
        String email = accountViewRepository.findEmailByNameAndMailAndPhone(
                request.getName(),
                request.getEmail(),
                request.getPhone()
        ).orElse(null);

        if (email != null) {
            String resetToken = jwtUtil.createPasswordResetToken(email);
            String resetLink = String.format(
                    "https://your-frontend-domain.com/reset-password?token=%s&email=%s",
                    resetToken, email
            );

            String subject = "비밀번호 재설정 안내";
            String content = String.format(
                    "안녕하세요, %s님\n비밀번호 재설정을 위해 아래 링크를 클릭해주세요:\n%s\n링크는 15분간 유효합니다.",
                    request.getName(), resetLink
            );

            emailService.sendEmail(email, subject, content);
        }
    }

    @Override
    public AccountTokenResponseDTO authenticateOAuthAccount(AccountOAuthRequestDTO request) {
        AccountView account = accountViewRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    // Create new user for OAuth
                    User newUser = User.builder()
                            .name(request.getName())
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .loginMethod(true)
                            .phone(request.getPhone())
                            .gender(request.getGender())
                            .birth(request.getBirth())
                            .zipcode(0)
                            .address("NOT_PROVIDED")
                            .detailAddress("NOT_PROVIDED")
                            .status(UserStatus.활성화)
                            .registeredAt(LocalDateTime.now())
                            .build();
                    userRepository.save(newUser);

                    return accountViewRepository.findByEmail(request.getEmail())
                            .orElseThrow(() -> new RuntimeException("Failed to create OAuth account"));
                });

        String accessToken = jwtUtil.createAccessToken(account.getEmail(), account.getRole());
        String refreshToken = jwtUtil.createRefreshToken(account.getEmail(), account.getRole());

        // Redis에 토큰 저장
        saveToken(account.getEmail(), accessToken, refreshToken);

        return AccountTokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userEmail(account.getEmail())
                .role(account.getRole())
                .build();
    }

    @Override
    public Map<String, Object> getNaverUserInfo(String code, String state) {
        // 1. 액세스 토큰 얻기
        String accessToken = getNaverAccessToken(code, state);

        // 2. 사용자 정보 요청
        return getNaverUserProfile(accessToken);
    }

    private String getNaverAccessToken(String code, String state) {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);
        params.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("access_token")) {
            return (String) response.getBody().get("access_token");
        }

        throw new RuntimeException("Failed to get access token from Naver");
    }

    private Map<String, Object> getNaverUserProfile(String accessToken) {
        String profileUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                profileUrl,
                HttpMethod.GET,
                request,
                Map.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to get user profile from Naver");
        }

        return response.getBody();
    }

    // Redis 관련 메서드들
    @Override
    public void saveToken(String email, String accessToken, String refreshToken) {
        // Access Token 저장
        redisTemplate.opsForValue().set(
                "AT:" + email,
                accessToken,
                ACCESS_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );

        // Refresh Token 저장
        redisTemplate.opsForValue().set(
                "RT:" + email,
                refreshToken,
                REFRESH_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );
    }
    @Override
    public String getStoredAccessToken(String email) {
        return (String) redisTemplate.opsForValue().get("AT:" + email);
    }
    @Override
    public String getStoredRefreshToken(String email) {
        return (String) redisTemplate.opsForValue().get("RT:" + email);
    }
    @Override
    public void deleteTokens(String email) {
        redisTemplate.delete("AT:" + email);
        redisTemplate.delete("RT:" + email);
    }
}