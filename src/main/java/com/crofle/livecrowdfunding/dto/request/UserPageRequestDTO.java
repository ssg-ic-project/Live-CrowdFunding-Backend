package com.crofle.livecrowdfunding.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageRequestDTO { //페이지 요청

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 10;

    private String orderBy = "id";

    private String orderByDir = "DESC";
}
