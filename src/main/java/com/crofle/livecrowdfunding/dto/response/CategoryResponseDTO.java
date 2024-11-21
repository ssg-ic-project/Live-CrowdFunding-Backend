package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String classification;
}
