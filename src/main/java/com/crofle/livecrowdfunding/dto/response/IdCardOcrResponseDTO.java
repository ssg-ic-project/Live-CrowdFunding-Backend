package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdCardOcrResponseDTO {
    private boolean success;
    private String name;
    private String birthDate;
    private String errorMessage;
}