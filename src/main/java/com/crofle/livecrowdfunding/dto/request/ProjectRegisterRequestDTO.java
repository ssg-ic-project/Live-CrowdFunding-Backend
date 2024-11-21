package com.crofle.livecrowdfunding.dto.request;

import com.crofle.livecrowdfunding.dto.response.EssentialDocumentDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRegisterRequestDTO {
    private Long makerId;
    private Long planId;
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
