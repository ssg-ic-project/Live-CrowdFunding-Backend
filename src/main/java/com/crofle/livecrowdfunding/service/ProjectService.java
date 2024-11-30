package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.response.ProjectWithConditionResponseDTO;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
  
    ProjectDetailWithLikedResponseDTO getProjectForUser(Long id, Long userId);

    ProjectDetailResponseDTO getProjectForLive(Long id);

    void createProject(ProjectRegisterRequestDTO requestDTO, List<MultipartFile> images, MultipartFile contentImage, List<MultipartFile> documents);

    void updateProjectStatus(Long id, ProjectStatusRequestDTO requestDTO);
    
    ProjectDetailForMakerResponseDTO getProjectForMaker(Long id);

    ProjectDetailToUpdateResponseDTO getProjectForManagerUpdate(Long id);

    void updateProject(Long id, ProjectUpdateRequestDTO requestDTO, List<MultipartFile> images, MultipartFile contentImage, List<MultipartFile> documents);

    PageListResponseDTO<ProjectListResponseDTO> getProjectList(Long id, int statusNumber, PageRequestDTO pageRequestDTO);

    ProjectMainResponseDTO getMainProjects();

    List<ProjectLiveVODResponseDTO> getLiveAndVODProjectList();

    PageListResponseDTO<ProjectWithConditionResponseDTO> getCategoryProjects(Long categoryId, PageRequestDTO pageRequestDTO);

    PageListResponseDTO<ProjectWithConditionResponseDTO> getSearchProjects(String keyword, PageRequestDTO pageRequestDTO);
}