package com.crofle.livecrowdfunding.config;

import com.crofle.livecrowdfunding.domain.enums.Role;
import com.crofle.livecrowdfunding.service.AccountService;
import com.crofle.livecrowdfunding.util.JwtUtil;
import com.crofle.livecrowdfunding.dto.request.AccountOAuthRequestDTO;
import com.crofle.livecrowdfunding.dto.response.AccountTokenResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final AccountService accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
//                        websocket endpoint 허용
                        .requestMatchers("/ws/**", "/sub/**", "/pub/**").permitAll()
                        .requestMatchers("/**").permitAll())
//                        .requestMatchers("/api/account/login", "/api/account/refresh").permitAll()
//                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
//                        .requestMatchers("/api/oauth2/**").permitAll()
//                        .requestMatchers("/login/oauth2/code/**").permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        .requestMatchers("/api/dashboard/**").permitAll()
//                        .requestMatchers("/api/admin-account/**").permitAll()
//                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService()))
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                        .failureHandler(oAuth2AuthenticationFailureHandler())
                );

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response,
                                                AuthenticationException exception) throws IOException {
                log.error("OAuth2 authentication failed: ", exception);
//                String targetUrl = "http://localhost:5173/login?error=" +
//                        URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
//                getRedirectStrategy().sendRedirect(request, response, targetUrl);
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5500"));
//        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "RefreshToken"));

        // WebSocket을 위한 CORS 설정 추가
        configuration.addAllowedOriginPattern("*");  // WebSocket의 경우 더 유연한 CORS 정책이 필요할 수 있음
        configuration.addAllowedHeader("Sec-WebSocket-Protocol");
        configuration.addAllowedHeader("Sec-WebSocket-Version");
        configuration.addAllowedHeader("Sec-WebSocket-Key");
        configuration.addAllowedHeader("Sec-WebSocket-Extensions");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        source.registerCorsConfiguration("/ws/**", configuration);
        return source;
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
        return userRequest -> {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oauth2User = delegate.loadUser(userRequest);

            try {
                log.debug("OAuth2User attributes: {}", oauth2User.getAttributes());
                Map<String, Object> attributes = oauth2User.getAttributes();
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");

                String email = (String) response.get("email");
                String name = (String) response.get("name");

                AccountOAuthRequestDTO authRequest = AccountOAuthRequestDTO.builder()
                        .email(email)
                        .name(name)
                        .role(Role.USER)
                        .build();

                accountService.authenticateOAuthAccount(authRequest);
                return oauth2User;
            } catch (Exception e) {
                log.error("Error processing OAuth2User: ", e);
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("processing_error", "Failed to process OAuth2User", null)
                );
            }
        };
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                Map<String, Object> attributes = oauth2User.getAttributes();
                Map<String, Object> responseData = (Map<String, Object>) attributes.get("response");

                String email = (String) responseData.get("email");
                String name = (String) responseData.get("name");

                try {
                    AccountOAuthRequestDTO authRequest = AccountOAuthRequestDTO.builder()
                            .email(email)
                            .name(name)
                            .role(Role.USER)
                            .build();

                    AccountTokenResponseDTO tokenResponse = accountService.authenticateOAuthAccount(authRequest);

                    String targetUrl = UriComponentsBuilder
                            .fromUriString("http://localhost:5173/oauth/callback")
                            .queryParam("token", tokenResponse.getAccessToken())
                            .queryParam("refreshToken", tokenResponse.getRefreshToken())
                            .queryParam("email", tokenResponse.getUserEmail())
                            .build().toUriString();

                    getRedirectStrategy().sendRedirect(request, response, targetUrl);
                } catch (Exception e) {
                    log.error("Error in OAuth2 success handler: ", e);
                    getRedirectStrategy().sendRedirect(request, response,
                            "http://localhost:5173/login?error=Authentication failed");
                }
            }
        };
    }
}