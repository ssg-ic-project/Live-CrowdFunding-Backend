package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDTO { //마이페이지에서 사용자 정보 조회
    private Long id;
    private String name;
    private String nickname;
    private String phone;
    private String email;
    private String address;
    private String detailAddress;
    private Boolean loginMethod;
    private Boolean notification;
}
