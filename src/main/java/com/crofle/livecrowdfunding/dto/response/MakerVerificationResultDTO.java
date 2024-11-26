package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MakerVerificationResultDTO {
    private boolean idCardVerified;
    private boolean employmentCertVerified;
    private boolean businessNumberVerified;
    private String verifiedName;
    private String verifiedBirthDate;
    private String verifiedCompanyName;
    private String verifiedBusinessNumber;
}