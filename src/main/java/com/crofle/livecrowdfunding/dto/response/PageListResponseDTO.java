package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.dto.PageInfoDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageListResponseDTO<T> {

    private PageInfoDTO pageInfoDTO;
    private List<T> dataList;

}
