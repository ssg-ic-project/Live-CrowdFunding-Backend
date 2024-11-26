package com.crofle.livecrowdfunding.dto.request;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MakerBaseInfoRequestDTO {
    private String email;
    private String password;
    private String phone;
    private Integer zipcode;
    private String address;
    private String detailAddress;
}
