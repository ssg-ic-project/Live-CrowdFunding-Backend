package com.crofle.livecrowdfunding.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserRequestDTO {
    private Long id;
    private String name;
    private String nickname;
    private String phone;
    private Boolean gender;
    private String birth;
    private String email;
    private String password;
    private Integer zipcode;
    private String address;
    private String detailAddress;
    private Boolean loginMethod;
    private Boolean notification;
    private List<Long> categoryIds;
}
