package com.crofle.livecrowdfunding.controller.api;

import com.crofle.livecrowdfunding.dto.request.AccountTokenRequestDTO;
import com.crofle.livecrowdfunding.dto.request.AdminAccountLoginRequestDTO;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
import com.crofle.livecrowdfunding.service.AdminAccountService;
import com.crofle.livecrowdfunding.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin-account")
@RequiredArgsConstructor
@Log4j2
public class AdminAccountRestController {
    private final AdminAccountService adminAccountService;
    private final JwtUtil jwtUtil;

    //로그인
    @PostMapping("/login")
    public ResponseEntity<AccountTokenResponseDTO>login(@RequestBody AdminAccountLoginRequestDTO request){
        log.info("check from controller");
        log.info(request.getIdentificationNumber());
        log.info(request.getPassword());
        return ResponseEntity.ok(adminAccountService.login(request));
    }

    //토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<AccountTokenResponseDTO>refresh(
            @RequestHeader(value = "Authorization", required = true) String refreshToken,
            @RequestHeader(value = "AccessToken", required = true) String accessToken){
        AccountTokenRequestDTO request = AccountTokenRequestDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(adminAccountService.refresh(request));
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String idNum = jwtUtil.getEmployeeNumberFromToken(token);
        adminAccountService.logout(idNum);
        return ResponseEntity.ok().build();
    }
}
