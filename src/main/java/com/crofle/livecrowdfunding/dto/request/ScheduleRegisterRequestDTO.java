package com.crofle.livecrowdfunding.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRegisterRequestDTO { // 라이브 스트리밍 예약 시 생성
    private Long projectId;
    private LocalDateTime date;
}
