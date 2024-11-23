package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.response.EventLogWithEventNameResponseDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.service.EventLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event-log")
public class EventLogController {
    private final EventLogService eventLogService;

    @GetMapping("/{userId}")
    public ResponseEntity<PageListResponseDTO<EventLogWithEventNameResponseDTO>> findByUser(@PathVariable Long userId, @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok().body(eventLogService.findByUser(userId, pageRequestDTO));
    }
}
