package com.crofle.livecrowdfunding.dto;

import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PageInfoDTO {
    private int page;
    private int size;
    private int total;

    private int start;
    private int end;

    private boolean prev;
    private boolean next;

    @Builder(builderMethodName = "withAll")
    public PageInfoDTO(PageRequestDTO pageRequestDTO, int total) {
        if (total <= 0) {
            return;
        }

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.total = total;

        this.end = (int)(Math.ceil(this.page / 10.0)) * 10;
        this.start = this.end - 9;

        int last = (int) (Math.ceil((total/(double)size)));
        this.end = end > last ? last : end;

        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }

}
