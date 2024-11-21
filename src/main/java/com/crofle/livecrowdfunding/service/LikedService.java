package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ProjectLikedResponseDTO;
import com.crofle.livecrowdfunding.dto.request.LikedRequestDTO;

import java.util.List;


public interface LikedService {
    boolean toggleLike(LikedRequestDTO likedRequestDTO);

    PageListResponseDTO<ProjectLikedResponseDTO> getUserLikedProjects(Long userId, PageRequestDTO pageRequestDTO);
}
