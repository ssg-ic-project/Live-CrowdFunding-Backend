package com.crofle.livecrowdfunding.service;


import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.*;

import java.util.List;

public interface ProjectService {
  
    ProjectDetailResponseDTO getProjectForUser(Long id);

    void createProject(ProjectRegisterRequestDTO requestDTO);

    void updateProjectStatus(Long id, ProjectStatusRequestDTO requestDTO);
    
    ProjectDetailForMakerResponseDTO getProjectForMaker(Long id);

    ProjectDetailToUpdateResponseDTO getProjectForManagerUpdate(Long id);

    void updateProject(Long id, ProjectUpdateRequestDTO requestDTO);

    PageListResponseDTO<ProjectListResponseDTO> getProjectList(ProjectListRequestDTO requestDTO, PageRequestDTO pageRequestDTO);

    ProjectMainResponseDTO getMainProjects();

    List<ProjectLiveVODResponseDTO> getLiveAndVODProjectList();
}