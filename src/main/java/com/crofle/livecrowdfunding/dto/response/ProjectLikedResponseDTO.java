package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.id.LikedId;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLikedResponseDTO {
    private Long id;
    private String productName;
    private String description;
    private String thumbnailUrl;
    private Integer price;
    private Integer percentage;
}
