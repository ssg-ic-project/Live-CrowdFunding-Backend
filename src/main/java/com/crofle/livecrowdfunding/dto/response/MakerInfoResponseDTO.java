package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakerInfoResponseDTO { // 판매자 정보 조회 응답 DTO
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String detailAddress;
}
