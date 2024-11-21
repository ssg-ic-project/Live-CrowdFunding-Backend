package com.crofle.livecrowdfunding.dto.request;

import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusRequestDTO { // 판매자가 프로젝트의 상태를 변경할 때 사용하는 DTO, 보통 수정 후 재검토 시
    private ProjectStatus status;
}
