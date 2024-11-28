package com.crofle.livecrowdfunding.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MakerVerificationRequestDTO {
    private String name;
    private String birthDate;
    private String companyName;
    private String businessNumber;
    private MultipartFile idCard;
    private MultipartFile employmentCert;
    private MakerBaseInfoRequestDTO makerInfo;
}