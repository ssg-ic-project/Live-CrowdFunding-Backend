package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.projection.EventLogWithEventNameDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.UserEventLogResponseDTO;
import com.crofle.livecrowdfunding.service.EventLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event-log")
public class EventLogController {
    private final EventLogService eventLogService;

    @GetMapping("/{userId}")
    public ResponseEntity<PageListResponseDTO<EventLogWithEventNameDTO>> findByUser(@PathVariable Long userId, @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok().body(eventLogService.findByUser(userId, pageRequestDTO));
    }
}
