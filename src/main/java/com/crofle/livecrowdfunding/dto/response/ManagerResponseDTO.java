package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ManagerResponseDTO {
    private int id;
    private String name;
}
