package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUserResponseDTO {
    private Long id;
    private String name;
    private String nickname;
    private String phone;
    private String email;
    private Integer zipcode;
    private String address;
    private String detailAddress;
}
