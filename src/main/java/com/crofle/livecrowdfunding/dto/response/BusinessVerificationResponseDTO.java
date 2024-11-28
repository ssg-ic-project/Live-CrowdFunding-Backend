package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationResponseDTO {
    private boolean success;
    private String companyName;
    private String businessNumber;
    private String status;
    private String errorMessage;
}
