package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
import com.crofle.livecrowdfunding.service.AccountService;
import com.crofle.livecrowdfunding.service.MakerService;
import com.crofle.livecrowdfunding.service.UserService;
import com.crofle.livecrowdfunding.util.JwtUtil;
import com.crofle.livecrowdfunding.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Log4j2
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;
    private final MakerService makerService;
    private final JwtUtil jwtUtil;

    /**
     * 일반 사용자 회원가입
     */
    @PostMapping("/register/user")
    public ResponseEntity<SaveUserRequestDTO> registerUser(@RequestBody SaveUserRequestDTO request) {
        return ResponseEntity.ok(userService.saveUser(request));
    }

    /**
     * 메이커 회원가입
     */
    @PostMapping("/register/maker")
    public ResponseEntity<SaveMakerRequestDTO> registerMaker(@RequestBody SaveMakerRequestDTO request) {
        return ResponseEntity.ok(makerService.saveMaker(request));
    }

    /**
     * 일반 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<AccountTokenResponseDTO> login(@RequestBody AccountLoginRequestDTO request) {
        return ResponseEntity.ok(accountService.login(request));
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<AccountTokenResponseDTO> refresh(
            @RequestHeader("Authorization") String refreshToken,
            @RequestHeader("AccessToken") String accessToken) {
        AccountTokenRequestDTO request = AccountTokenRequestDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(accountService.refresh(request));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.getEmailFromToken(token);
        accountService.logout(email);
        return ResponseEntity.ok().build();
    }

    /**
     * 이메일 찾기
     */
    @PostMapping("/find-email")
    public ResponseEntity<?> findEmail(@RequestParam String name, @RequestParam String phone) {
        AccountFindEmailRequestDTO request = AccountFindEmailRequestDTO.builder()
                .name(name)
                .phone(phone)
                .build();
        return accountService.findEmailByNameAndPhone(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 비밀번호 재설정 이메일 발송
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody AccountPasswordResetRequestDTO request) {
        accountService.sendResetPasswordEmail(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 네이버 로그인 URL 제공
     */
    @GetMapping("/oauth/naver")
    public ResponseEntity<String> getNaverLoginUrl() {
        String state = UUID.randomUUID().toString(); // 보안을 위한 랜덤 상태값
        String naverLoginUrl = String.format("https://nid.naver.com/oauth2.0/authorize" +
                        "?response_type=code" +
                        "&client_id=%s" +
                        "&redirect_uri=%s" +
                        "&state=%s",
                "Rb3vhJIp1oI1q3_oAJiB",
                "http://localhost:8080/login/oauth2/code/naver",
                state);

        log.info("Generated Naver login URL with state: {}", state);
        return ResponseEntity.ok(naverLoginUrl);
    }

    /**
     * 네이버 로그인 콜백 처리
     */
    @GetMapping("/oauth/naver/callback")
    public ResponseEntity<AccountTokenResponseDTO> naverCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) {
        log.info("Received Naver callback - code: {}, state: {}", code, state);

        try {
            // SecurityConfig에서 처리된 OAuth 인증 결과를 받아서 처리
            Map<String, Object> attributes = accountService.getNaverUserInfo(code, state);

            AccountOAuthRequestDTO authRequest = AccountOAuthRequestDTO.fromNaverResponse(
                    (Map<String, Object>) attributes.get("response")
            );

            AccountTokenResponseDTO tokenResponse = accountService.authenticateOAuthAccount(authRequest);
            log.info("OAuth login successful for email: {}", authRequest.getEmail());

            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            log.error("Error processing Naver callback", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 프런트엔드 리다이렉트 처리
     */
    @GetMapping("/oauth/callback")
    public ResponseEntity<Void> handleOAuthCallback(
            @RequestParam("token") String token,
            @RequestParam("refreshToken") String refreshToken) {
        // 프런트엔드 URL로 리다이렉트
        String redirectUrl = String.format("http://localhost:5173/oauth/callback?" +
                "token=%s&refreshToken=%s", token, refreshToken);

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    /**
     * OAuth 상태 확인
     */
    @GetMapping("/oauth/status")
    public ResponseEntity<String> checkOAuthStatus() {
        return ResponseEntity.ok("OAuth service is running");
    }
}