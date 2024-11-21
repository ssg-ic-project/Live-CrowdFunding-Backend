package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.enums.DocumentType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DocumentResponseDTO {  // 마이페이지에서 서류 리턴

    private String id;
    private String url;
    private String name;
    private String docType;
}
