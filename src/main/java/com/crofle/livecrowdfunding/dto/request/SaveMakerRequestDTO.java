package com.crofle.livecrowdfunding.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SaveMakerRequestDTO {
    // 실제 저장될 정보
    private String name;        // 실명 (이걸 그대로 저장)
    private String email;
    private String password;
    private String phone;
    private Integer zipcode;
    private String address;
    private String detailAddress;
    private Integer business;   // 실제 저장될 사업자번호

    // 검증용 정보 (저장되지 않음)
    private String birthDate;   // 생년월일 (검증용)
    private String companyName; // 회사명 (검증용)
    private String businessNumber; // 사업자번호 문자열 (검증용)
    private MultipartFile idCard;
    private MultipartFile employmentCert;
}
