package com.crofle.livecrowdfunding.dto.response;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMainResponseDTO {
    private List<TopFundingInMainResponseDTO> topFundingProjects;
    private List<LiveFundingInMainResponseDTO> liveFundingProjects;
}
