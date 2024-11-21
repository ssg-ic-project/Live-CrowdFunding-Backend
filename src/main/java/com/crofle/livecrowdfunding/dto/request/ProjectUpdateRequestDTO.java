package com.crofle.livecrowdfunding.dto.request;

import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequestDTO {
    private Long categoryId;
    private String productName;
    private String summary;
    private Integer price;
    private Integer discountPercentage;
    private Integer goalAmount;
    private String contentImage;
    private List<ImageRegisterRequestDTO> images;
    private List<DocumentRegisterRequestDTO> essentialDocuments;
}
