package com.crofle.livecrowdfunding.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SaveMakerRequestDTO {
    private Long id;
    private String name;
    private String phone;
    private Long business;
    private String email;
    private String password;
    private Integer zipcode;
    private String address;
    private String detailAddress;
}
