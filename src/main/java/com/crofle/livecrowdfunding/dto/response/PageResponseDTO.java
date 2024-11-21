package com.crofle.livecrowdfunding.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponseDTO<T> {
    private T data;
}
