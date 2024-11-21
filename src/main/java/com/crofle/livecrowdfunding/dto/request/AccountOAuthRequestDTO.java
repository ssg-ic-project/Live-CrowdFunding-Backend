package com.crofle.livecrowdfunding.dto.request;

import com.crofle.livecrowdfunding.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountOAuthRequestDTO {
    private String email;
    private String name;
    private String phone;
    private Boolean gender;
    private String birth;
    private Role role;
    private Boolean loginMethod;

    public static AccountOAuthRequestDTO fromNaverResponse(Map<String, Object> response) {
        return AccountOAuthRequestDTO.builder()
                .email((String) response.get("email"))
                .name((String) response.get("name"))
                .phone(formatPhoneNumber((String) response.get("mobile")))  // 메서드 이름 수정됨
                .gender("M".equals(response.get("gender")))
                .birth((String) response.get("birthyear"))
                .role(Role.USER)
                .loginMethod(true)
                .build();
    }

    private static String formatPhoneNumber(String phone) {
        // 모든 하이픈과 공백 제거
        String cleanNumber = phone.replaceAll("[- ]", "");

        // xxx-xxxx-xxxx 형식으로 포맷팅
        if (cleanNumber.length() == 11) {
            return String.format("%s-%s-%s",
                    cleanNumber.substring(0, 3),
                    cleanNumber.substring(3, 7),
                    cleanNumber.substring(7));
        }
        // 기타 형식의 경우 원본 반환
        return phone;
    }
}