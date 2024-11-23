package com.crofle.livecrowdfunding.dto.request;

import com.crofle.livecrowdfunding.dto.SearchTypeDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    @Min(value = 1)
    @Positive
    private int page = 1;

    @Builder.Default
    @Min(value = 10)
    private int size = 10;

    @Builder.Default
    private String orderBy = "id";

    @Builder.Default
    private String orderByDir = "DESC";

    @Builder.Default
    private SearchTypeDTO search = new SearchTypeDTO();

    //검색 조건으로 프로젝트 이름명 조회 가능!!!
    private String projectName;

    //User 관리용
    private String userName;

    //카테고리 관리용 (카테고리 확장 시 ENUM으로 변경하는 것이 좋을듯)
    private Long categoryId;

    public int getOffset(){
        return (page - 1) * 10;
    }

    public Pageable getPageable() {
        if (orderBy != null && orderByDir != null) {
            Sort.Direction dir = Sort.Direction.fromString(orderByDir.toUpperCase());
            return PageRequest.of(page - 1, size, Sort.by(dir, orderBy));
        }
        return PageRequest.of(page - 1, size);
    }
}
