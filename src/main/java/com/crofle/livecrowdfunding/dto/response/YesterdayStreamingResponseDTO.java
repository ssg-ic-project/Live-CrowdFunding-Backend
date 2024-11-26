package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class YesterdayStreamingResponseDTO {
    private String projectName;
    private Long totalViewers;
    private Long totalBuyers;
}
