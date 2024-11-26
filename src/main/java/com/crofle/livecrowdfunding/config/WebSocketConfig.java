package com.crofle.livecrowdfunding.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");  // 메시지 구독 경로
        config.setApplicationDestinationPrefixes("/pub");   // 메시지 발행 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("웹소켓 연결 시도");
        registry.addEndpoint("/ws")
//                .setAllowedOrigins("*") // Vue.js 개발 서버
                .setAllowedOriginPatterns("*");
//                .withSockJS(); // client가 SockJS를 사용할 시 사용
//                .setWebSocketEnabled(true);
    }
}