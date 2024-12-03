package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.entity.Image;
import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.entity.Schedule;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectVodResponseDTO {
    private Long projectId;
    private String productName;
    private String imageUrl;
    private Integer percentage;
    private Short isStreaming;
    private Long scheduleId;
    private String classification;
    private Long remainingTime;

    public static ProjectVodResponseDTO from(Project project, Schedule schedule) {
        return ProjectVodResponseDTO.builder()
                .projectId(project.getId())
                .productName(project.getProductName())
                .imageUrl(project.getImages().get(0).getUrl())
                .percentage(project.getPercentage())
                .isStreaming(schedule.getIsStreaming())
                .scheduleId(schedule.getId())
//                .classification(project.getCategory().getName())
//                .remainingTime(schedule.getRemainingTime())
                .build();
    }
}
