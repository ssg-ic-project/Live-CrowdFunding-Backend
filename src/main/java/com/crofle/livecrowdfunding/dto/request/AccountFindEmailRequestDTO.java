package com.crofle.livecrowdfunding.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 이메일 찾기 요청
public class AccountFindEmailRequestDTO {
    private String name;
    private String phone;
}
