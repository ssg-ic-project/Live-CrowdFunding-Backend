package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetailWithLikedResponseDTO { // 사용자가 프로젝트 조회 시 리턴
    private Long id;
    private String maker;
    private String productName;
    private String category;
    private String summary;
    private Integer price;
    private Integer discountPercentage;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer percentage;
    private Integer goalAmount;
    private String contentImage;
    private List<ImageResponseDTO> images;
    private Integer likeCount;
    private Boolean isLiked;
    private Short isStreaming;
    private Long scheduleId;
}
