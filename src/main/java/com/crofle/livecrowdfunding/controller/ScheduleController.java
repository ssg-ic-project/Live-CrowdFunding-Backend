package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.request.ScheduleRegisterRequestDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleChartResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleReserveResponseDTO;
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

    @PostMapping("/create")
    public ResponseEntity<Void> createSchedule(@RequestBody ScheduleRegisterRequestDTO requestDTO) {
        scheduleService.createSchedule(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ScheduleReserveResponseDTO>> getReserveSchedule() {
        return ResponseEntity.ok().body(scheduleService.getReserveSchedule(LocalDateTime.now()));
    }

    @GetMapping("/chart")
    public ResponseEntity<List<ScheduleChartResponseDTO>> getScheduleChart() {
        return ResponseEntity.ok().body(scheduleService.getScheduleChart(LocalDateTime.now()));
    }
}
