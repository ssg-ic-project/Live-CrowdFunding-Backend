package com.crofle.livecrowdfunding.dto.request;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImageRegisterRequestDTO {
    private String url;
    private Integer imageNumber;
    private String name;
}
