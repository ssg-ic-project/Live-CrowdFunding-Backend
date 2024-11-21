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
public class ProjectListRequestDTO { //프로젝트 리스트 조회 요청
    private int statusNumber; //1 진행 전(검토중, 반려), 2 진행 중(펀딩중), 3 종료(성공, 실패)
}
