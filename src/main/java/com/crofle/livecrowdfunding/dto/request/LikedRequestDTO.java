package com.crofle.livecrowdfunding.dto.request;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LikedRequestDTO {
    private Long userId;
    private Long projectId;
}
