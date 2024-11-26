package com.crofle.livecrowdfunding.util;

import com.crofle.livecrowdfunding.domain.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_VALIDITY = 30 * 60 * 1000L; // 30분
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L; // 7일
    private static final long PASSWORD_RESET_TOKEN_VALIDITY = 15 * 60 * 1000L; // 15분

    /**
     * 액세스 토큰 생성
     */
    public String createAccessToken(String email, Role role) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(String email, Role role) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    /**
     * 비밀번호 재설정 토큰 생성
     */
    public String createPasswordResetToken(String email) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("purpose", "password_reset");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + PASSWORD_RESET_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }


    /**
     * 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = parseToken(token);
            // 만료 시간 검증
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmailFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.error("Failed to get email from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }

    /**
     * 토큰에서 역할 추출
     */
    public Role getRoleFromToken(String token) {
        try {
            String role = parseToken(token).getBody().get("role", String.class);
            return Role.valueOf(role);
        } catch (Exception e) {
            log.error("Failed to get role from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }

    /**
     * 토큰의 만료 시간 추출
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            return parseToken(token).getBody().getExpiration();
        } catch (Exception e) {
            log.error("Failed to get expiration date from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }

    /**
     * 토큰이 비밀번호 재설정용인지 확인
     */

    public boolean isPasswordResetToken(String token) {
        try {
            Claims claims = parseToken(token).getBody();
            return "password_reset".equals(claims.get("purpose", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰 파싱
     */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token);
    }

    /**
     * 서명 키 생성
     */
    private Key getSigningKey() {
        byte[] apiKeySecretBytes = Base64.getEncoder().encode(secretKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * 토큰 남은 유효 시간 계산 (밀리초)
     */
    public long getTokenTimeToLive(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 토큰이 만료 임박한지 확인 (5분 미만 남은 경우)
     */
    public boolean isTokenNearExpiration(String token) {
        final long FIVE_MINUTES = 5 * 60 * 1000L;
        return getTokenTimeToLive(token) < FIVE_MINUTES;
    }


}