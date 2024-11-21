package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO { //단일 주문 정보 리턴
    private Long id;
    private OrderUserResponseDTO user;
    private OrderProjectResponseDTO project;
    private Integer amount;
    private Integer paymentPrice;
}
