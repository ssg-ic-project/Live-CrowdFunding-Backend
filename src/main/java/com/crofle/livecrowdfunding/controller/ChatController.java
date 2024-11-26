package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.ChatMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class ChatController {

    private final RedisTemplate<String, Object> redisTemplate;

    public ChatController(@Qualifier("chatRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageDTO chatMessage) {
        String roomId = chatMessage.getRoomId();
        chatMessage.setTimestamp(LocalDateTime.now().toString());

        // Redis에 메시지 발행
        String channel = "/sub/chat/" + roomId;
        redisTemplate.convertAndSend(channel, chatMessage);

        // Redis에 채팅 기록 저장
//        saveChatMessage(roomId, chatMessage);
    }
}