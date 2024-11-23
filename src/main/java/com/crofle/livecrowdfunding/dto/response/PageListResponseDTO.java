package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.dto.PageInfoDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class PageListResponseDTO<T> {

    private PageInfoDTO pageInfoDTO;
    private List<T> dataList;

}
