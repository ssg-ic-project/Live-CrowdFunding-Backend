package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.response.EventLogWithEventNameResponseDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;

public interface EventLogService {
    PageListResponseDTO<EventLogWithEventNameResponseDTO> findByUser(Long userId, PageRequestDTO pageRequestDTO);
}
