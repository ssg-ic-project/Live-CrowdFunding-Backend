package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.ScheduleRegisterRequestDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleChartResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleReserveResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Log4j2
@RequiredArgsConstructor
public class ScheduleServiceTest {
    @Autowired
    private ScheduleService scheduleService;

    @Test
    public void createScheduleTest() {
        Long projectId = 1L;
        LocalDateTime date = LocalDateTime.now();

        scheduleService.createSchedule(ScheduleRegisterRequestDTO.builder()
                .projectId(projectId)
                .date(date)
                .build());

        log.info("Schedule created");
    }

    @Test
    public void getReserveScheduleTest() {
        LocalDateTime startDate = LocalDateTime.now();

        List<ScheduleReserveResponseDTO> list = scheduleService.getReserveSchedule(startDate);

        log.info(list);
    }

    @Test
    public void getScheduleChartTest() {
        LocalDateTime startDate = LocalDateTime.now();

        List<ScheduleChartResponseDTO> list = scheduleService.getScheduleChart(startDate);

        log.info(list);
    }
}
