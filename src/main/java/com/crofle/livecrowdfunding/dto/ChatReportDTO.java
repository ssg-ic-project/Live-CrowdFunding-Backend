package com.crofle.livecrowdfunding.dto;

import com.crofle.livecrowdfunding.domain.entity.ChatReport;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatReportDTO {
    private Long id;
    private String userName;
    private String projectName;
    private String managerName;
    private String reason;
    private String chatMessage;
    private LocalDateTime createdAt;

    public static ChatReportDTO from(ChatReport report) {
        return ChatReportDTO.builder()
                .id(report.getId())
                .userName(report.getUser().getName())
                .projectName(report.getProject().getProductName())
                .managerName(report.getManager().getName())
                .reason(report.getReason())
                .chatMessage(report.getChatMessage())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
