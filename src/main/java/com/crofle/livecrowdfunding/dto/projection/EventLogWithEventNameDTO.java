package com.crofle.livecrowdfunding.dto.projection;

import com.crofle.livecrowdfunding.domain.entity.EventLog;
import com.crofle.livecrowdfunding.domain.id.EventLogId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventLogWithEventNameDTO {
    private LocalDateTime winningDate;
    private String winningPrize;
    private String eventName;
}
