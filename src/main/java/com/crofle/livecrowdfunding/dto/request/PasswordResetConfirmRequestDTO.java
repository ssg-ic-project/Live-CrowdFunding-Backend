package com.crofle.livecrowdfunding.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetConfirmRequestDTO {
    private String token;
    private String email;
    private String newPassword;
}