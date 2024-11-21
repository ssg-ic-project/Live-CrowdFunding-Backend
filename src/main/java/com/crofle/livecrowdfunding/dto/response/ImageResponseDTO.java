package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDTO {
    private Long id;
    private String url;
    private String name;
    private String imageNumber;
}
