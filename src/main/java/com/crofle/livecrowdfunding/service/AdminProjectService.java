package com.crofle.livecrowdfunding.service;
import com.crofle.livecrowdfunding.dto.request.ProjectApprovalRequestDTO;
import com.crofle.livecrowdfunding.dto.response.EssentialDocumentDTO;
import com.crofle.livecrowdfunding.dto.response.ImageResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ProjectResponseInfoDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;

import java.util.List;

public interface AdminProjectService {
    PageListResponseDTO<ProjectResponseInfoDTO> findProjectList(PageRequestDTO pageRequestDTO);
    ProjectResponseInfoDTO findProject(Long id);

    List<EssentialDocumentDTO> findEssentialDocs(Long id);
    List<ImageResponseDTO>findProjectImages(Long id);

    void updateApprovalStatus(Long id, ProjectApprovalRequestDTO comment);


}
