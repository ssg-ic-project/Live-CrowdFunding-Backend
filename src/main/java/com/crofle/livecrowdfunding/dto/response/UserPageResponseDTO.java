package com.crofle.livecrowdfunding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageResponseDTO<T> {
    private List<T> content;
    private int page;
    private int size;
    private int totalPage;
    private long totalElements;
    private boolean last;
    private boolean first;
    private boolean empty;

    public UserPageResponseDTO(List<T> content, Page<?> page) {
        this.content = content;
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPage = page.getTotalPages();
        this.last = page.isLast();
        this.first = page.isFirst();
        this.empty = page.isEmpty();
    }

}
