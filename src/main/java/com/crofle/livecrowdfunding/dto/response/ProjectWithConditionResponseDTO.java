package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectWithConditionResponseDTO { //검색 및 카테고리로 접속 시 보여지는 리스트 dto 근데 live dto랑 같아서 합쳐도 될듯
    private Long projectId;
    private String imageUrl;
    private String productName;
    private Integer percentage;
    private String classification;
    private Long remainingTime;
    private Boolean isStreaming;
}
