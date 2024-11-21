package com.crofle.livecrowdfunding.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMakerResponseDTO {  //프로젝트 상세 페이지에 필요한 메이커 정보
    private Long id;
    private String name;
}
