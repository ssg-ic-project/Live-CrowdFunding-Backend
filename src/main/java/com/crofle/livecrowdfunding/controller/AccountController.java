
    package com.crofle.livecrowdfunding.controller;

    import com.crofle.livecrowdfunding.dto.request.*;
    import com.crofle.livecrowdfunding.dto.response.AccountFindEmailResponseDTO;
    import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
    import com.crofle.livecrowdfunding.service.AccountService;
    import com.crofle.livecrowdfunding.util.JwtUtil;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.log4j.Log4j2;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.ErrorResponse;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;
    import java.util.UUID;

    @RestController
    @RequestMapping("/api/account")
    @RequiredArgsConstructor
    @Log4j2
    public class AccountController {
        private final AccountService accountService;
        private final JwtUtil jwtUtil;

        @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
        private String naverAuthBaseUrl;

        @Value("${spring.security.oauth2.client.registration.naver.client-id}")
        private String naverClientId;

        @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
        private String naverRedirectUri;

        @Value("${spring.mvc.cors.allowed-origins}")
        private String frontendBaseUrl;

        //일반 로그인
        @PostMapping("/login")
        public ResponseEntity<AccountTokenResponseDTO> login(@RequestBody AccountLoginRequestDTO request) {
            log.info("Login attempt for email: {}", request.getEmail());
            AccountTokenResponseDTO response = accountService.login(request);
            log.info("Login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        }

        //토큰 갱신
        @PostMapping("/refresh")
        public ResponseEntity<AccountTokenResponseDTO> refresh(
                @RequestHeader("Authorization") String refreshToken,
                @RequestHeader("AccessToken") String accessToken) {
            log.info("Token refresh requested");
            AccountTokenRequestDTO request = AccountTokenRequestDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            AccountTokenResponseDTO response = accountService.refresh(request);
            log.info("Token refresh successful");
            return ResponseEntity.ok(response);
        }

        // 로그아웃
        @PostMapping("/logout")
        public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
            String email = jwtUtil.getEmailFromToken(token);
            log.info("Logout request for user: {}", email);
            accountService.logout(email);
            log.info("Logout successful for user: {}", email);
            return ResponseEntity.ok().build();
        }

        //이메일 찾기
        @PostMapping("/find-email")
        public ResponseEntity<?> findEmail(
                @RequestParam String name,
                @RequestParam String phone) {
            log.info("Find email request for name: {}", name);
            AccountFindEmailRequestDTO request = AccountFindEmailRequestDTO.builder()
                    .name(name)
                    .phone(phone)
                    .build();
            return accountService.findEmailByNameAndPhone(request)
                    .map(email -> {
                        log.info("Email found for name: {}", name);
                        return ResponseEntity.ok(AccountFindEmailResponseDTO.builder()
                                .email(email.getEmail())
                                .build());
                    })
                    .orElseGet(() -> {
                        log.info("No email found for name: {}", name);
                        return ResponseEntity.notFound().build();
                    });
        }

        // 비밀번호 재설정 이메일 발송
        @PostMapping("/reset-password/email")
        public ResponseEntity<?> sendResetPasswordEmail(@RequestBody AccountPasswordResetRequestDTO request) {
            try {
                accountService.sendResetPasswordEmail(request);
                return ResponseEntity.ok("Reset password email sent successfully");
            } catch (Exception e) {
                log.error("Failed to send reset password email", e);
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        // 비밀번호 재설정 토큰 검증
        @GetMapping("/reset-password/validate")
        public ResponseEntity<?> validateResetToken(@RequestParam String token) {
            try {
                boolean isValid = accountService.validateResetToken(token);
                if (!isValid) {
                    return ResponseEntity.badRequest().body("Invalid or expired token");
                }
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Token validation failed", e);
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        // 비밀번호 재설정 처리
        @PostMapping("/reset-password/confirm")
        public ResponseEntity<?> resetPassword(@RequestBody PasswordResetConfirmRequestDTO request) {
            try {
                accountService.resetPassword(request.getToken(), request.getEmail(), request.getNewPassword());
                return ResponseEntity.ok("Password reset successfully");
            } catch (Exception e) {
                log.error("Password reset failed", e);
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }


        //    네이버 로그인 URL 제공
        @GetMapping("/oauth/naver")
        public ResponseEntity<String> getNaverLoginUrl() {
            String state = UUID.randomUUID().toString();
            String naverLoginUrl = String.format("%s" +
                            "?response_type=code" +
                            "&client_id=%s" +
                            "&redirect_uri=%s" +
                            "&state=%s",
                    naverAuthBaseUrl,
                    naverClientId,
                    naverRedirectUri,
                    state);

            log.info("Generated Naver login URL with state: {}", state);
            return ResponseEntity.ok(naverLoginUrl);
        }

        // 네이버 로그인 콜백 처리
        @GetMapping("/oauth/naver/callback")
        public ResponseEntity<AccountTokenResponseDTO> naverCallback(
                @RequestParam("code") String code,
                @RequestParam("state") String state) {
            log.info("Received Naver callback - code: {}, state: {}", code, state);

            Map<String, Object> attributes = accountService.getNaverUserInfo(code, state);
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");

            AccountOAuthRequestDTO authRequest = AccountOAuthRequestDTO.fromNaverResponse(response);
            AccountTokenResponseDTO tokenResponse = accountService.authenticateOAuthAccount(authRequest);

            log.info("OAuth login successful for email: {}", authRequest.getEmail());
            return ResponseEntity.ok(tokenResponse);
        }

        //OAuth 콜백 리다이렉트 처리
        @GetMapping("/oauth/callback")
        public ResponseEntity<Void> handleOAuthCallback(
                @RequestParam("token") String token,
                @RequestParam("refreshToken") String refreshToken) {
            log.info("Processing OAuth callback redirect");
            String redirectUrl = String.format("%s/oauth/callback?" +
                            "token=%s&refreshToken=%s",
                    frontendBaseUrl, token, refreshToken);

            return ResponseEntity.status(302)
                    .header("Location", redirectUrl)
                    .build();
        }

        /**
         * OAuth 상태 확인
         */
        @GetMapping("/oauth/status")
        public ResponseEntity<String> checkOAuthStatus() {
            log.info("Checking OAuth service status");
            return ResponseEntity.ok("OAuth service is running");
        }
    }