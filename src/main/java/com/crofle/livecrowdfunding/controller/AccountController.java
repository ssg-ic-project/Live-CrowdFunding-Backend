    package com.crofle.livecrowdfunding.controller;

    import com.crofle.livecrowdfunding.dto.request.*;
    import com.crofle.livecrowdfunding.dto.response.*;
    import com.crofle.livecrowdfunding.service.AccountService;
    import com.crofle.livecrowdfunding.service.MakerService;
    import com.crofle.livecrowdfunding.service.UserService;
    import com.crofle.livecrowdfunding.util.JwtUtil;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.log4j.Log4j2;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.ErrorResponse;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

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

        private final UserService userService;
        private final MakerService makerService;

        //일반 로그인
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody AccountLoginRequestDTO request) {
            try {
                log.info("Login attempt for email: {}", request.getEmail());
                AccountTokenResponseDTO response = accountService.login(request);
                log.info("Login successful for email: {}", request.getEmail());
                return ResponseEntity.ok(response);
            } catch (IllegalArgumentException e) {
                // 잘못된 이메일/비밀번호의 경우 400 에러 반환
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new loginErrorResponseDTO("Invalid email or password"));
            } catch (Exception e) {
                // 기타 서버 에러는 500 유지
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new loginErrorResponseDTO(e.getMessage()));
            }
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
        public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
            try {
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    String email = jwtUtil.getEmailFromToken(token);
                    accountService.logout(email);
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.badRequest().build();
            } catch (Exception e) {
                log.error("Logout failed: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
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

        //OAuth 상태 확인
        @GetMapping("/oauth/status")
        public ResponseEntity<String> checkOAuthStatus() {
            log.info("Checking OAuth service status");
            return ResponseEntity.ok("OAuth service is running");
        }

        @PostMapping("/send-verification")
        public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestParam String email) {
            String verificationCode = accountService.sendVerificationEmail(email);
            return ResponseEntity.ok(Map.of("message", "인증 코드가 발송되었습니다."));
        }
        @GetMapping("/check-email")
        public ResponseEntity<?> checkEmailDuplicate(@RequestParam String email) {
            boolean isDuplicate = accountService.isEmailDuplicate(email);
            return ResponseEntity.ok(Map.of(
                    "isDuplicate", isDuplicate,
                    "message", isDuplicate ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다."
            ));
        }

        @PostMapping("/verify-code")
        public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
            boolean isVerified = accountService.verifyCode(email, code);
            return ResponseEntity.ok(Map.of(
                    "verified", isVerified,
                    "message", isVerified ? "인증이 완료되었습니다." : "잘못된 인증 코드입니다."
            ));
        }

        // 일반 회원가입
        @PostMapping("/signup/user")
        public ResponseEntity<?> signUpUser(@RequestBody SaveUserRequestDTO request) {
            try {
                userService.saveUser(request);
                return ResponseEntity.ok().body(Map.of("message", "회원가입이 완료되었습니다."));
            } catch (IllegalArgumentException e) {
                log.error("User registration failed: {}", e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                log.error("User registration failed", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "회원가입 처리 중 오류가 발생했습니다."));
            }
        }

        // 메이커 회원가입
        @PostMapping("/signup/maker")
        public ResponseEntity<?> signUpMaker(@ModelAttribute SaveMakerRequestDTO request) {
            try {
                log.info("Maker registration requested for: {}", request.getEmail());
                makerService.saveMaker(request);

                return ResponseEntity.ok().body(Map.of(
                        "message", "회원가입이 완료되었습니다.",
                        "success", true
                ));
            } catch (IllegalArgumentException e) {
                log.error("Maker registration validation failed: {}", e.getMessage());
                return ResponseEntity.badRequest().body(Map.of(
                        "message", e.getMessage(),
                        "success", false
                ));
            } catch (Exception e) {
                log.error("Maker registration failed", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "message", "회원가입 처리 중 오류가 발생했습니다.",
                                "success", false
                        ));
            }
        }

        @PostMapping("/signup/maker/verify/id-card")
        public ResponseEntity<?> verifyIdCard(@RequestParam("file") MultipartFile file) {
            try {
                log.info("ID card verification requested");
                IdCardOcrResponseDTO response = accountService.verifyIdCard(file);

                if (response.isSuccess()) {
                    log.info("ID card verification successful");
                    return ResponseEntity.ok(response);
                } else {
                    log.warn("ID card verification failed: {}", response.getErrorMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                log.error("ID card verification error", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "신분증 인증 처리 중 오류가 발생했습니다."));
            }
        }

        @PostMapping("/signup/maker/verify/employment")
        public ResponseEntity<?> verifyEmploymentCert(@RequestParam("file") MultipartFile file) {
            try {
                log.info("Employment certificate verification requested");
                EmploymentCertOcrResponseDTO response = accountService.verifyEmploymentCert(file);

                if (response.isSuccess()) {
                    log.info("Employment certificate verification successful");
                    return ResponseEntity.ok(response);
                } else {
                    log.warn("Employment certificate verification failed: {}", response.getErrorMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                log.error("Employment certificate verification error", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "재직증명서 인증 처리 중 오류가 발생했습니다."));
            }
        }

        @GetMapping("/signup/maker/verify/business")
        public ResponseEntity<?> verifyBusinessNumber(@RequestParam String businessNumber) {
            try {
                log.info("Business number verification requested: {}", businessNumber);
                BusinessVerificationResponseDTO response = accountService.verifyBusinessNumber(businessNumber);

                if (response.isSuccess()) {
                    log.info("Business number verification successful");
                    return ResponseEntity.ok(response);
                } else {
                    log.warn("Business number verification failed: {}", response.getErrorMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                log.error("Business number verification error", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "사업자번호 검증 중 오류가 발생했습니다."));
            }
        }
    }