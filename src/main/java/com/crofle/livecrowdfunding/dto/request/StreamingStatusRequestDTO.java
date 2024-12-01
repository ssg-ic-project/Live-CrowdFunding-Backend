package com.crofle.livecrowdfunding.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StreamingStatusRequestDTO {
    private Long projectId;
    private int index;
}
