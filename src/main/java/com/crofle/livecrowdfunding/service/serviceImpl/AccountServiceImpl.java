package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.config.BusinessVerificationConfig;
import com.crofle.livecrowdfunding.config.ClovaOcrConfig;
import com.crofle.livecrowdfunding.domain.entity.User;
import com.crofle.livecrowdfunding.domain.enums.UserStatus;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.BusinessVerificationResponseDTO;
import com.crofle.livecrowdfunding.dto.response.EmploymentCertOcrResponseDTO;
import com.crofle.livecrowdfunding.dto.response.IdCardOcrResponseDTO;
import com.crofle.livecrowdfunding.repository.MakerRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
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
    private final MakerRepository makerRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;
    private final ClovaOcrConfig clovaOcrConfig;
    private final BusinessVerificationConfig businessVerificationConfig;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirectUri;

    private static final String RESET_TOKEN_PREFIX = "password_reset:";
    private static final long RESET_TOKEN_EXPIRE_TIME = 15 * 60;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;    // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일


    @Override
    public AccountTokenResponseDTO login(AccountLoginRequestDTO request) {
        // 이메일로 사용자 찾기
        AccountView account = accountViewRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        log.info("Found account: {}", account);
        log.info("Input password: {}, Stored password: {}", request.getPassword(), account.getPassword());

        // 비밀번호 검증
        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), account.getPassword());
        log.info("Password match result: {}", passwordMatch);

        if (!passwordMatch) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // 토큰 생성
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

            // Redis에 토큰 저장 (unused 상태로) - 직접 저장 시도
            String redisKey = RESET_TOKEN_PREFIX + resetToken;
            try {
                redisTemplate.opsForValue().set(redisKey, "unused", RESET_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
                log.info("Token stored in Redis: key={}, status=unused", redisKey);

                // 저장 확인
                String status = (String) redisTemplate.opsForValue().get(redisKey);
                log.info("Verified token in Redis: key={}, status={}", redisKey, status);
            } catch (Exception e) {
                log.error("Failed to store token in Redis", e);
                throw new RuntimeException("Failed to store reset token", e);
            }

            String resetLink = String.format(
                    "http://localhost:5173/auth/reset-password?token=%s&email=%s",
                    resetToken, email
            );

            String subject = "비밀번호 재설정 안내";
            String content = String.format(
                    "<div style='margin:30px auto;max-width:600px;padding:20px;font-family:Arial,sans-serif;'>" +
                            "<h2 style='color:#333;margin-bottom:20px;'>비밀번호 재설정 안내</h2>" +
                            "<p style='color:#666;line-height:1.6;margin-bottom:20px;'>안녕하세요, %s님</p>" +
                            "<p style='color:#666;line-height:1.6;margin-bottom:20px;'>아래 버튼을 클릭하여 비밀번호를 재설정해주세요.</p>" +
                            "<a href='%s' style='display:inline-block;background-color:#007bff;color:#ffffff;text-decoration:none;" +
                            "padding:12px 30px;border-radius:5px;margin:20px 0;'>비밀번호 재설정</a>" +
                            "<p style='color:#999;font-size:14px;margin-top:20px;'>링크는 15분간 유효하며, 1회만 사용 가능합니다.</p>" +
                            "<p style='color:#999;font-size:14px;'>본 메일은 발신전용입니다.</p>" +
                            "</div>",
                    request.getName(), resetLink
            );

            emailService.sendHtmlEmail(email, subject, content);
            log.info("Password reset email sent to: {}", email);
        } else {
            log.warn("No user found with the provided information");
        }
    }

    @Override
    public boolean validateResetToken(String token) {
        try {
            log.info("Validating reset token: {}", token);

            if (!jwtUtil.validateToken(token) || !jwtUtil.isPasswordResetToken(token)) {
                log.warn("Invalid token or not a password reset token");
                return false;
            }

            String redisKey = RESET_TOKEN_PREFIX + token;
            String tokenStatus = (String) redisTemplate.opsForValue().get(redisKey);
            log.info("Token status from Redis: {}", tokenStatus);

            if (tokenStatus == null) {
                log.warn("Reset token not found in Redis: {}", token);
                return false;
            }

            if (!"unused".equals(tokenStatus)) {
                log.warn("Reset token already used: {}", token);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Error validating reset token", e);
            return false;
        }
    }

    @Transactional
    @Override
    public void resetPassword(String token, String email, String newPassword) {
        try {
            log.info("Attempting to reset password for email: {}", email);

            // 토큰 검증
            if (!validateResetToken(token)) {
                log.error("Token validation failed during password reset");
                throw new IllegalArgumentException("Invalid or expired reset token");
            }

            // 사용자 찾기
            AccountView accountView = accountViewRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("User not found: {}", email);
                        return new IllegalArgumentException("User not found");
                    });

            // 새 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(newPassword);
            log.info("Password encrypted successfully");

            // 비밀번호 업데이트
            try {
                if (accountView.getRole() == Role.USER) {
                    userRepository.updatePassword(accountView.getId(), encodedPassword);
                    log.info("User password updated successfully");
                } else if (accountView.getRole() == Role.MAKER) {
                    makerRepository.updatePassword(accountView.getId(), encodedPassword);
                    log.info("Maker password updated successfully");
                }
            } catch (Exception e) {
                log.error("Failed to update password in database", e);
                throw new RuntimeException("Failed to update password in database", e);
            }

            // 토큰 사용 처리
            String redisKey = RESET_TOKEN_PREFIX + token;
            redisTemplate.opsForValue().set(redisKey, "used", RESET_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
            log.info("Token marked as used in Redis");

        } catch (Exception e) {
            log.error("Failed to reset password", e);
            throw new IllegalArgumentException("Failed to reset password: " + e.getMessage());
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

    // 인증 코드 생성 및 발송 메서드
    @Override
    public String sendVerificationEmail(String email) {
        // 이메일 중복 체크
        if (isEmailDuplicate(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String verificationCode = generateVerificationCode();

        // 이메일 내용 구성
        String subject = "회원가입 인증 코드";
        String content = String.format(
                "<div style='margin:30px auto;max-width:600px;padding:20px;font-family:Arial,sans-serif;'>" +
                        "<h2 style='color:#333;margin-bottom:20px;'>회원가입 인증 코드</h2>" +
                        "<p style='color:#666;line-height:1.6;margin-bottom:20px;'>안녕하세요,</p>" +
                        "<p style='color:#666;line-height:1.6;margin-bottom:20px;'>회원가입을 위한 인증 코드입니다:</p>" +
                        "<div style='background-color:#f8f9fa;padding:15px;border-radius:5px;text-align:center;'>" +
                        "<h1 style='color:#007bff;letter-spacing:5px;font-size:24px;margin:0;'>%s</h1>" +
                        "</div>" +
                        "<p style='color:#999;font-size:14px;margin-top:20px;'>인증 코드는 10분간 유효합니다.</p>" +
                        "</div>",
                verificationCode
        );

        // HTML 형식의 이메일 발송
        emailService.sendHtmlEmail(email, subject, content);

        // Redis에 인증 코드 저장 (10분 유효)
        redisTemplate.opsForValue().set(
                "EMAIL_VERIFY:" + email,
                verificationCode,
                10,
                TimeUnit.MINUTES
        );

        log.info("Verification code sent to: {}", email);
        return verificationCode;
    }

    // 인증 코드 생성 메서드
    @Override
    public String generateVerificationCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    // 인증 코드 확인 메서드
    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = (String) redisTemplate.opsForValue().get("EMAIL_VERIFY:" + email);

        if (code.equals(storedCode)) {
            // 인증 성공 시 Redis에서 인증 코드 삭제
            redisTemplate.delete("EMAIL_VERIFY:" + email);
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmailDuplicate(String email) {
        return accountViewRepository.findByEmail(email).isPresent();
    }

    @Override
    public IdCardOcrResponseDTO verifyIdCard(MultipartFile idCard) {
        try {
            log.info("Processing ID card OCR verification");

            // 파일을 Base64로 인코딩
            String base64Image = Base64.getEncoder().encodeToString(idCard.getBytes());

            // Clova OCR API 요청 준비
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-OCR-SECRET", clovaOcrConfig.getSecret());

            Map<String, Object> requestJson = new HashMap<>();
            requestJson.put("images", Arrays.asList(
                    Map.of(
                            "format", "jpg",
                            "name", "id_card",
                            "data", base64Image
                    )
            ));

            requestJson.put("requestId", UUID.randomUUID().toString());
            requestJson.put("version", "V2");
            requestJson.put("timestamp", System.currentTimeMillis());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestJson, headers);

            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    clovaOcrConfig.getUrl(),
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            log.info("OCR Response: {}", response.getBody());

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                List<Map<String, Object>> images = (List<Map<String, Object>>) responseBody.get("images");
                if (!images.isEmpty()) {
                    Map<String, Object> image = images.get(0);
                    List<Map<String, Object>> fields = (List<Map<String, Object>>) image.get("fields");

                    String name = extractField(fields, "inferText", "성명:");
                    String birthDate = extractField(fields, "inferText", "생년월일:");

                    log.info("ID card OCR successful for name: {}", name);

                    return IdCardOcrResponseDTO.builder()
                            .success(true)
                            .name(name)
                            .birthDate(birthDate)
                            .build();
                }
            }

            throw new IllegalStateException("Failed to extract information from OCR response");

        } catch (Exception e) {
            log.error("ID card OCR verification failed", e);
            return IdCardOcrResponseDTO.builder()
                    .success(false)
                    .errorMessage("ID card verification failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public EmploymentCertOcrResponseDTO verifyEmploymentCert(MultipartFile employmentCert) {
        try {
            log.info("Processing employment certificate OCR verification");

            String base64Image = Base64.getEncoder().encodeToString(employmentCert.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-OCR-SECRET", clovaOcrConfig.getSecret());

            Map<String, Object> requestJson = new HashMap<>();
            requestJson.put("images", Arrays.asList(
                    Map.of(
                            "format", "jpg",
                            "name", "employment_cert",
                            "data", base64Image
                    )
            ));
            requestJson.put("requestId", UUID.randomUUID().toString());
            requestJson.put("version", "V2");
            requestJson.put("timestamp", System.currentTimeMillis());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestJson, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    clovaOcrConfig.getUrl(),
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                List<Map<String, Object>> images = (List<Map<String, Object>>) responseBody.get("images");
                if (!images.isEmpty()) {
                    Map<String, Object> image = images.get(0);
                    List<Map<String, Object>> fields = (List<Map<String, Object>>) image.get("fields");

                    log.info("Employment certificate OCR fields: {}", fields);

                    String name = null;
                    StringBuilder birthDate = new StringBuilder();
                    String companyName = null;
                    String businessNumber = null;

                    for (int i = 0; i < fields.size(); i++) {
                        String inferText = (String) fields.get(i).get("inferText");

                        if (inferText == null) continue;

                        // 이름 추출
                        if (inferText.equals("성명") && i + 1 < fields.size()) {
                            name = (String) fields.get(i + 1).get("inferText");
                        }

                        // 생년월일 추출 (YYYY년 MM월 DD일 형식을 YYYYMMDD로 변환)
                        if (inferText.equals("생년월일") && i + 3 < fields.size()) {
                            String year = ((String) fields.get(i + 1).get("inferText")).replace("년", "");
                            String month = ((String) fields.get(i + 2).get("inferText")).replace("월", "");
                            String day = ((String) fields.get(i + 3).get("inferText")).replace("일", "");

                            // 한 자리 월/일인 경우 앞에 0 추가
                            if (month.length() == 1) month = "0" + month;
                            if (day.length() == 1) day = "0" + day;

                            birthDate.append(year).append(month).append(day);
                        }

                        // 회사명 추출 ("주식회사" + "카카오" 조합)
                        if (inferText.equals("주식회사") && i + 1 < fields.size()) {
                            companyName = inferText + " " + fields.get(i + 1).get("inferText");
                        }

                        // 사업자등록번호 추출
                        if (inferText.equals("사업자등록번호:") && i + 1 < fields.size()) {
                            businessNumber = ((String) fields.get(i + 1).get("inferText")).replace("-", "");
                        }
                    }

                    log.info("Extracted fields - name: {}, birthDate: {}, companyName: {}, businessNumber: {}",
                            name, birthDate.toString(), companyName, businessNumber);

                    return EmploymentCertOcrResponseDTO.builder()
                            .success(name != null && birthDate.length() > 0 && companyName != null && businessNumber != null)
                            .name(name)
                            .birthDate(birthDate.toString())
                            .companyName(companyName)
                            .businessNumber(businessNumber)
                            .build();
                }
            }

            throw new IllegalStateException("Failed to extract information from OCR response");

        } catch (Exception e) {
            log.error("Employment certificate OCR verification failed", e);
            return EmploymentCertOcrResponseDTO.builder()
                    .success(false)
                    .errorMessage("Employment certificate verification failed: " + e.getMessage())
                    .build();
        }
    }
    @Override
    public BusinessVerificationResponseDTO verifyBusinessNumber(String businessNumber) {
        try {
            log.info("Verifying business number: {}", businessNumber);

            // 디코딩된 키로 시도
            String decodedKey = "LF2yp4tUCnVipycSkdnMWASPiNhXderoqsbrjHkSmn3SvjG61Wk9McAJ2gMcyPj2QefAiyoJVJd08WIO47s4LQ==";

            // 요청 본문 구성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, List<String>> requestBody = new HashMap<>();
            requestBody.put("b_no", Arrays.asList(businessNumber.replace("-", "")));

            HttpEntity<Map<String, List<String>>> request = new HttpEntity<>(requestBody, headers);

            // URL을 직접 구성 (URL 인코딩 없이)
            String url = businessVerificationConfig.getUrl() + "?serviceKey=" + decodedKey;
            log.info("Request URL: {}", url);

            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            // 응답 처리는 동일
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                log.info("Response body: {}", responseBody);  // 응답 로깅 추가
                List<Map<String, String>> data = (List<Map<String, String>>) responseBody.get("data");
                if (!data.isEmpty()) {
                    Map<String, String> result = data.get(0);
                    String status = result.get("b_stt_cd");
                    String companyStatus = result.get("b_stt");

                    boolean isValid = "01".equals(status);
                    log.info("Business verification successful. Status: {}", companyStatus);

                    return BusinessVerificationResponseDTO.builder()
                            .success(isValid)
                            .companyName(result.get("b_nm"))
                            .businessNumber(businessNumber)
                            .status(companyStatus)
                            .build();
                }
            }

            throw new IllegalStateException("Failed to verify business number");

        } catch (Exception e) {
            log.error("Business number verification failed: {}", e.getMessage());
            return BusinessVerificationResponseDTO.builder()
                    .success(false)
                    .errorMessage("Business verification failed: " + e.getMessage())
                    .build();
        }
    }

    private String extractField(List<Map<String, Object>> fields, String textKey, String labelText) {
        int labelIndex = -1;
        for (int i = 0; i < fields.size(); i++) {
            String inferText = (String) fields.get(i).get(textKey);
            if (inferText != null && inferText.startsWith(labelText)) {
                labelIndex = i;
                break;
            }
        }

        if (labelIndex != -1 && labelIndex + 1 < fields.size()) {
            String value = (String) fields.get(labelIndex + 1).get(textKey);
            // 생년월일 필드인 경우 형식 변환
            if (labelText.contains("생년월일")) {
                // "YYYY.MM.DD" 형식에서 점(.)을 제거
                return value.replace(".", "");
            }
            return value;
        }

        return null;
    }
}