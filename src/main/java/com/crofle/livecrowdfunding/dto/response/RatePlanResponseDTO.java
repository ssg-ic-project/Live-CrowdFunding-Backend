package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RatePlanResponseDTO {
    private int id;
    private String planName;
    private short charge;
}
