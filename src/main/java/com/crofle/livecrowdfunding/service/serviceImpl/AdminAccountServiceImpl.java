package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.AdminAccountView;
import com.crofle.livecrowdfunding.domain.entity.Manager;
import com.crofle.livecrowdfunding.domain.enums.Role;
import com.crofle.livecrowdfunding.dto.request.AccountTokenRequestDTO;
import com.crofle.livecrowdfunding.dto.request.AdminAccountLoginRequestDTO;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
import com.crofle.livecrowdfunding.repository.AdminAccountRepository;
import com.crofle.livecrowdfunding.service.AdminAccountService;
import com.crofle.livecrowdfunding.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Log4j2
@RequiredArgsConstructor
public class AdminAccountServiceImpl implements AdminAccountService {
    private final AdminAccountRepository adminAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;    // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일

    @Override
    public AccountTokenResponseDTO login(AdminAccountLoginRequestDTO request) {
        //해당 사번의 직원이 존재하는지 DB 확인
        AdminAccountView manager = adminAccountRepository.findByIdNum(request.getIdentificationNumber())
                .orElseThrow(()-> new IllegalArgumentException("Invalid identification number or password "));

        log.info("사용된 PasswordEncoder 클래스: {}", passwordEncoder.getClass().getName());
        log.info("Found Manager: {}", manager);
        log.info("Input password: {}, Stored password: {}", request.getPassword(), manager.getPassword());

        //비번 확인
        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), manager.getPassword());
        log.info("Password match result: {}", passwordMatch);

        // 임시로 평문 비밀번호 비교
//        if(!request.getPassword().equals(manager.getPassword())) {
//            throw new IllegalArgumentException("Invalid password");
//        }

        if(!passwordMatch){
            throw new IllegalArgumentException("Invalid identification number or password");
        }

        //토큰 발급
        String accessToken = jwtUtil.createAccessToken(manager.getIdNum(), Role.Employeer);
        String refreshToken = jwtUtil.createRefreshToken(manager.getIdNum(), Role.Employeer);

        log.info(accessToken, refreshToken);
        //Redis 토큰 저장
        saveToken(manager.getIdNum(), accessToken, refreshToken);

        return AccountTokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .identificationNumber(manager.getIdNum())
                .build();
    }

    @Override
    public void saveToken(String idNum, String accessToken, String refreshToken) {
        // Access Token 저장
        redisTemplate.opsForValue().set(
                "AT:" + idNum,
                accessToken,
                ACCESS_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );

        // Refresh Token 저장
        redisTemplate.opsForValue().set(
                "RT:" + idNum,
                refreshToken,
                REFRESH_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public AccountTokenResponseDTO refresh(AccountTokenRequestDTO request) {
        // 1. 리프레시 토큰 검증
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // 2. Redis에서 저장된 토큰 확인
        String idNum = jwtUtil.getEmployeeNumberFromToken(request.getRefreshToken());
        String storedRefreshToken = getStoredRefreshToken(idNum);

        if (!request.getRefreshToken().equals(storedRefreshToken)) {
            throw new IllegalArgumentException("Refresh token not found");
        }

        // 3. 실제 사용자가 존재하는지 확인
        AdminAccountView manager = adminAccountRepository.findByIdNum(idNum)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 4. 새로운 토큰 생성
        String newAccessToken = jwtUtil.createAccessToken(manager.getIdNum(), Role.Employeer);
        String newRefreshToken = jwtUtil.createRefreshToken(manager.getIdNum(), Role.Employeer);

        // 5. Redis에 새로운 토큰 저장
        saveToken(idNum, newAccessToken, newRefreshToken);

        return AccountTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .identificationNumber(manager.getIdNum())
                .role(Role.Employeer)
                .build();
    }
    @Override
    public String getStoredRefreshToken(String employeeNumber) {
        return (String) redisTemplate.opsForValue().get("RT:" + employeeNumber);
    }

    @Override
    public void logout(String identificationNumber) {
        //redis 토큰 삭제
        deleteTokens(identificationNumber);
        log.info("Admin logged out and tokens deleted: {}", identificationNumber);
    }
    @Override
    public void deleteTokens(String identificationNumber) {
        redisTemplate.delete("AT:" + identificationNumber);
        redisTemplate.delete("RT:" + identificationNumber);
    }

}
