package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.ChatMessageDTO;
import com.crofle.livecrowdfunding.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api")
public class ChatController {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatService chatService;
//    private final BlockService blockService;
//    private final ChatReportService chatReportService;

    public ChatController(
            @Qualifier("chatRedisTemplate") RedisTemplate<String, Object> redisTemplate,
            ChatService chatService,
            ObjectMapper objectMapper
//            BlockService blockService,
//            ChatReportService chatReportService
    ) {
        this.redisTemplate = redisTemplate;
        this.chatService = chatService;
        this.objectMapper = objectMapper;
//        this.blockService = blockService;
//        this.chatReportService = chatReportService;
    }

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageDTO chatMessage) {
        String roomId = chatMessage.getRoomId();
        chatMessage.setTimestamp(LocalDateTime.now().toString());

        // 원본 메시지 로깅
        log.info("Original message: {}", chatMessage.getContent());

        // 비속어 필터링
        String filteredContent = chatService.filterProfanity(chatMessage.getContent());
        log.info("Filtered message: {}", filteredContent);  // 필터링된 결과 확인

        chatMessage.setContent(filteredContent);

        // Redis에 메시지 발행
        String channel = "/sub/chat/" + roomId;
        redisTemplate.convertAndSend(channel, chatMessage);

        // Redis에 채팅 기록 저장 전 상태 확인
        log.info("Saving message to Redis: {}", chatMessage);

        // Redis에 채팅 기록 저장
        saveChatMessage(roomId, chatMessage);
    }

//    @GetMapping("/chat/history/{roomId}")
//    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(
//            @PathVariable String roomId,
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        String userId = userDetails.getUsername();
//        Set<String> blockedUsers = blockService.getBlockedUsers(userId);
//
//        List<ChatMessageDTO> chatHistory = getChatMessages(roomId)
//                .stream()
//                .filter(msg -> !blockedUsers.contains(msg.getUserName()))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(chatHistory);
//    }

    // 사용자 차단
//    @PostMapping("/users/block")
//    public ResponseEntity<Void> blockUser(
//            @RequestBody BlockUserRequest request,
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        blockService.blockUser(userDetails.getUsername(), request.getBlockedUserName());
//        return ResponseEntity.ok().build();
//    }
//
//    // 차단 해제
//    @DeleteMapping("/users/unblock")
//    public ResponseEntity<Void> unblockUser(
//            @RequestBody BlockUserRequest request,
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        blockService.unblockUser(userDetails.getUsername(), request.getBlockedUserName());
//        return ResponseEntity.ok().build();
//    }
//
//    // 차단 목록 조회
//    @GetMapping("/users/blocked-list")
//    public ResponseEntity<Set<String>> getBlockedUsers(
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        Set<String> blockedUsers = blockService.getBlockedUsers(userDetails.getUsername());
//        return ResponseEntity.ok(blockedUsers);
//    }
//
    // 채팅 신고
//    @PostMapping("/chat-reports")
//    public ResponseEntity<ChatReportDTO> reportChat(
//            @Valid @RequestBody CreateChatReportRequest request,
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        ChatReportDTO report = chatReportService.createReport(request, userDetails.getUsername());
//        return ResponseEntity.ok(report);
//    }

    private void saveChatMessage(String roomId, ChatMessageDTO message) {
        String key = "chat:room:" + roomId;
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.opsForList().trim(key, -100, -1);
    }

    @GetMapping("/chat/history/{roomId}")
    private List<ChatMessageDTO> getChatMessages(@PathVariable(value = "roomId") String roomId) {
        String key = "chat:room:" + roomId;
        Long size = redisTemplate.opsForList().size(key);
        log.info("redis size = {}", size);

        if (size == null || size == 0) {
            return new ArrayList<>();
        }

        return Objects.requireNonNull(redisTemplate.opsForList()
                        .range(key, 0, -1))
                .stream()
                .map(msg -> {
                    try {
                        // LinkedHashMap을 JSON string으로 변환 후 ChatMessageDTO로 변환
                        return objectMapper.convertValue(msg, ChatMessageDTO.class);
                    } catch (Exception e) {
                        log.error("Failed to convert message: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull) // null 값 필터링
                .collect(Collectors.toList());
    }
}