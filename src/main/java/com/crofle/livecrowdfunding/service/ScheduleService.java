package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.domain.entity.Script;
import com.crofle.livecrowdfunding.domain.entity.Video;
import com.crofle.livecrowdfunding.dto.request.ScheduleRegisterRequestDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleReserveResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    void createSchedule(ScheduleRegisterRequestDTO requestDTO);

    List<ScheduleReserveResponseDTO> getReserveSchedule(LocalDateTime startDateTime);
}
