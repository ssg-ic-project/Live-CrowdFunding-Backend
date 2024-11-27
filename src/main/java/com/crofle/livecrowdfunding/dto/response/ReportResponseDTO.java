package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.entity.ChatReport;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponseDTO {
    private String message;
    private int status;
}