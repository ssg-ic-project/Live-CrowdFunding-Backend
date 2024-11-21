package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.UserMgmResponseDTO;

public interface AdminUserMgmService {
    PageListResponseDTO<UserMgmResponseDTO> getAllMembers(PageRequestDTO pageRequestDTO);
}
