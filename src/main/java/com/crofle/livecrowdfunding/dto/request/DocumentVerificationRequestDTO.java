package com.crofle.livecrowdfunding.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVerificationRequestDTO {
    private MultipartFile file;
    private String documentType; // ID_CARD or EMPLOYMENT_CERT
}