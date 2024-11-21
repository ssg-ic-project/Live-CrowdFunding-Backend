package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.enums.DocumentType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EssentialDocumentDTO {
    private String id;
    private String url;
    private String name;
    private DocumentType docType;
}
