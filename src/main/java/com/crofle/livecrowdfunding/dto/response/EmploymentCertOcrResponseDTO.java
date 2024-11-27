package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentCertOcrResponseDTO {
    private boolean success;
    private String name;
    private String birthDate;
    private String companyName;
    private String businessNumber;
    private String errorMessage;
}