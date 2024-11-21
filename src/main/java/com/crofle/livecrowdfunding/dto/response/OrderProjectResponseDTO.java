package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProjectResponseDTO {  // 주문 정보에 담기는 프로젝트 정보
    private Long id;
    private String productName;
    private String summary;
    private Integer price;
    private Integer discountPercentage;
    private Integer percentage;
    private List<ImageResponseDTO> urls;
}
