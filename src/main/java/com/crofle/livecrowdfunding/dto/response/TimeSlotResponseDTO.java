package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotResponseDTO {
    private String time;
    private boolean available;
    private int remainingSlots;

    public TimeSlotResponseDTO(LocalDateTime dateTime, boolean available, int remainingSlots) {
        this.time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.available = available;
        this.remainingSlots = remainingSlots;
    }
}
