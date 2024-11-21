package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 이메일 찾기 응답
public class AccountFindEmailResponseDTO {
    private String email;
    private String name;
}
