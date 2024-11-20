package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventLogWithEventNameResponseDTO {
    private LocalDateTime winningDate;
    private String winningPrize;
    private String eventName;
}
