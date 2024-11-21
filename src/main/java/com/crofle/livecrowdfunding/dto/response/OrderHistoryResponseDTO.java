package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryResponseDTO {
    private LocalDateTime paymentAt;       // 결제 시간
    private String projectImage;           // 프로젝트 이미지
    private String productName;            // 프로젝트명 (가독성을 위해 추가)
    private int amount;                    // 주문 수량
    private int paymentPrice;
}
