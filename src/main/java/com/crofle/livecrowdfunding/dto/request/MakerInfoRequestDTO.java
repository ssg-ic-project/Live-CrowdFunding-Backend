package com.crofle.livecrowdfunding.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakerInfoRequestDTO { // 판매자 정보 수정 요청 DTO
    private String phone;
    private String address;
    private String detailAddress;
}
