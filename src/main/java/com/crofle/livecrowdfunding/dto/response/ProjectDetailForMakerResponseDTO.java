package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDetailForMakerResponseDTO { // 판매자 프로젝트 조회, 프로젝트 정보와 미니 대시보드 리턴
    private String productName;
    private String showStatus;
    private String category;    //서비스에서 검토 로직있어야함
    private Integer price;
    private Integer discountPercentage;
    private Integer goalAmount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer paymentCount;
    private Integer currentSales;
    private Integer percentage;
    private List<ImageResponseDTO> images;
    private List<DocumentResponseDTO> essentialDocuments;
    private String summary;
    private String contentImage;
    private Integer remainingLiveCount;
    private Short isStreaming;
    private Long scheduleId;
    private LocalDateTime scheduleDate;
    private String rejectionReason;
}
