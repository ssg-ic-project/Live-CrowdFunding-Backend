package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleReserveResponseDTO { //라이브 방송 예약 가능한 시간을 담는 DTO
    private LocalDate date;
    private List<TimeSlotResponseDTO> timeSlot;
}
