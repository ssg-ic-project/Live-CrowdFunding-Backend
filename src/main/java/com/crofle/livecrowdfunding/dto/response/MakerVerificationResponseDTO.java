package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MakerVerificationResponseDTO {
    private boolean success;
    private String message;
    private MakerVerificationResultDTO verificationResult;
}