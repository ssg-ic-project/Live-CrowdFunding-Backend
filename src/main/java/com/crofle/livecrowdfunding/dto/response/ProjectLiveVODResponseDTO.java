package com.crofle.livecrowdfunding.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.NamedStoredProcedureQueries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectLiveVODResponseDTO {
    private Long projectId;
    private Long scheduleId;
    private String imageUrl;
    private String productName;
    private Integer percentage;
    private String classification;
    private Long remainingTime;
    @JsonProperty("isStreaming")
    private boolean isStreaming;
}
