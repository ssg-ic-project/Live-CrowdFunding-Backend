package com.crofle.livecrowdfunding.dto.request;

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
public class UserInfoRequestDTO { // 사용자 정보 수정 요청
    private String name;
    private String nickname;
    private String phone;
    private String address;
    private String detailAddress;
    private Boolean notification;
}
