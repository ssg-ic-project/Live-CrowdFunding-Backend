package com.crofle.livecrowdfunding.dto.request;

import com.crofle.livecrowdfunding.domain.enums.DocumentType;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRegisterRequestDTO {
    private String url;
    private DocumentType docType;
    private String name;
}
