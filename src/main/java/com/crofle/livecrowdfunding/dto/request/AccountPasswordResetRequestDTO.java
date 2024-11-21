package com.crofle.livecrowdfunding.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountPasswordResetRequestDTO {
    private String email;
    private String name;
    private String phone;
}
