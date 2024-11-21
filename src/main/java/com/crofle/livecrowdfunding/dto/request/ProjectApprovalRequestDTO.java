package com.crofle.livecrowdfunding.dto.request;

import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectApprovalRequestDTO {
    private ProjectStatus status;
    private String rejectionReason;
}
