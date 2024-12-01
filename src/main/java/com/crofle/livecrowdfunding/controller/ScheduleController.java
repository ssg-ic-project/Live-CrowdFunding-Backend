package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.request.ScheduleRegisterRequestDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleChartResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleReserveResponseDTO;
import com.crofle.livecrowdfunding.exception.ErrorResponse;
import com.crofle.livecrowdfunding.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/create") // 예외처리 시 제네릭 타입을 쓰는군 예외처리는 나중에 상태코드로 관리할 예정
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleRegisterRequestDTO requestDTO) {
        try {
            scheduleService.createSchedule(requestDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ScheduleReserveResponseDTO>> getReserveSchedule() {
        return ResponseEntity.ok().body(scheduleService.getReserveSchedule(LocalDateTime.now()));
    }

    @GetMapping("/chart")
    public ResponseEntity<List<ScheduleChartResponseDTO>> getScheduleChart() {
        return ResponseEntity.ok().body(scheduleService.getScheduleChart(LocalDateTime.now()));
    }
    
    @PatchMapping("/update/{scheduleId}")
    public ResponseEntity<Void> updateScheduleStatus(@PathVariable Long scheduleId) {
        scheduleService.updateScheduleStatus(scheduleId);
        return ResponseEntity.ok().build();
    }
}
