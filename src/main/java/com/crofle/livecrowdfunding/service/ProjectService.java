package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.response.ProjectWithConditionResponseDTO;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
  
    ProjectDetailResponseDTO getProjectForUser(Long id, Long userId);

    void createProject(ProjectRegisterRequestDTO requestDTO, List<MultipartFile> images, List<MultipartFile> documents);

    void updateProjectStatus(Long id, ProjectStatusRequestDTO requestDTO);
    
    ProjectDetailForMakerResponseDTO getProjectForMaker(Long id);

    ProjectDetailToUpdateResponseDTO getProjectForManagerUpdate(Long id);

    void updateProject(Long id, ProjectUpdateRequestDTO requestDTO);

    PageListResponseDTO<ProjectListResponseDTO> getProjectList(Long id, int statusNumber, PageRequestDTO pageRequestDTO);

    ProjectMainResponseDTO getMainProjects();

    List<ProjectLiveVODResponseDTO> getLiveAndVODProjectList();

    PageListResponseDTO<ProjectWithConditionResponseDTO> getCategoryProjects(Long categoryId, PageRequestDTO pageRequestDTO);

    PageListResponseDTO<ProjectWithConditionResponseDTO> getSearchProjects(String keyword, PageRequestDTO pageRequestDTO);
}