package com.crofle.livecrowdfunding.subscriber;

import com.crofle.livecrowdfunding.dto.ChatMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 받은 메시지 역직렬화
            String publishMessage = new String(message.getBody());
            ChatMessageDTO chatMessage = objectMapper.readValue(publishMessage, ChatMessageDTO.class);

            // 채팅방별 구독 주소로 메시지 전달
            String destination = "/sub/chat/" + chatMessage.getRoomId();
            messagingTemplate.convertAndSend(destination, chatMessage);

            log.info("Redis Subscriber :: {} -> {}", chatMessage.getRoomId(), chatMessage.getContent());
        } catch (JsonProcessingException e) {
            log.error("Error processing message: {}", e.getMessage());
        }
    }
}