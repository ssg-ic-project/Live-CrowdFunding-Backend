package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetailToUpdateResponseDTO {
    private String productName;
    private String summary;
    private String category;
    private Integer price;
    private Integer discountPercentage;
    private Integer percentage;
    private Integer goalAmount;
    private String contentImage;
    private List<ImageResponseDTO> images;
    private List<DocumentResponseDTO> essentialDocuments;
}
