package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.request.UserStatusRequestDTO;
import com.crofle.livecrowdfunding.dto.response.ChatReportDetailResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ChatReportListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;

public interface AdminChatReportService {
    PageListResponseDTO<ChatReportListResponseDTO> findReportList(PageRequestDTO pageRequestDTO);

    ChatReportDetailResponseDTO findReportDetail(Long reportId);

    void updateUserStatus(UserStatusRequestDTO updateDTO);

    Long getUserIdByReportId(Long reportId);
}
