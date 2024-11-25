package com.crofle.livecrowdfunding.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateChatReportRequest {
    @NotNull
    private Long projectId;

    @NotBlank
    private String reason;

    @NotBlank
    private String chatMessage;
}