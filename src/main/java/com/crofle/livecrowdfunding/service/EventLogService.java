package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.projection.EventLogWithEventNameDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.UserEventLogResponseDTO;

import java.util.List;

public interface EventLogService {
    PageListResponseDTO<EventLogWithEventNameDTO> findByUser(Long userId, PageRequestDTO pageRequestDTO);
}
