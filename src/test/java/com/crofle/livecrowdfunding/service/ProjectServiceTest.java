package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.domain.enums.DocumentType;
import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Log4j2
public class ProjectServiceTest {
    @Autowired
    private ProjectService projectService;

    @Test
    public void testFindProjectDetail() {
        Long id = 1L;
        ProjectDetailResponseDTO projectDetailResponseDTO = projectService.getProjectForUser(id);
        log.info(projectDetailResponseDTO);
    }

    @Test
    public void testCreateProject() {
         ProjectRegisterRequestDTO requestDTO = ProjectRegisterRequestDTO.builder()
                 .makerId(1L)
                 .planId(1L)
                 .categoryId(2L)
                 .productName("상태 테스팅 상품")
                 .summary("이건 좀 비쌀듯")
                 .price(1000000)
                 .discountPercentage(10)
                 .goalAmount(1000000000)
                 .contentImage("contentImage.jpg")
                 .images(List.of(
                         ImageRegisterRequestDTO.builder()
                                 .url("image1.jpg")
                                 .imageNumber(1)
                                 .name("이미지1")
                                 .build(),
                         ImageRegisterRequestDTO.builder()
                                 .url("image2.jpg")
                                 .imageNumber(2)
                                 .name("이미지2")
                                 .build()
                 ))
                 .essentialDocuments(List.of(
                         DocumentRegisterRequestDTO.builder()
                                 .url("document1.pdf")
                                 .docType(DocumentType.개발)
                                 .name("필수문서1")
                                 .build(),
                         DocumentRegisterRequestDTO.builder()
                                 .url("document2.pdf")
                                 .docType(DocumentType.프로젝트)
                                 .name("필수문서2")
                                 .build()
                 ))
                 .build();

         projectService.createProject(requestDTO);
    }

    @Test
    public void testUpdateProjectStatus() {
        Long projectId = 8L;

        ProjectStatusRequestDTO requestDTO = ProjectStatusRequestDTO.builder()
                .status(ProjectStatus.반려)
                .build();

        projectService.updateProjectStatus(projectId, requestDTO);
    }

    @Test
    public void testFindProjectDetailForMaker() {
        Long id = 1L;
        ProjectDetailForMakerResponseDTO projectDetailForMakerResponseDTO = projectService.getProjectForMaker(id);
        log.info(projectDetailForMakerResponseDTO);
    }

    @Test
    public void testFindProjectUpdate() {
        Long id = 1L;
        ProjectDetailToUpdateResponseDTO projectDetailToUpdateResponseDTO = projectService.getProjectForManagerUpdate(id);
        log.info(projectDetailToUpdateResponseDTO);
    }

    @Test
    public void testUpdateProject() {
        Long projectId = 1L;

        ProjectUpdateRequestDTO requestDTO = ProjectUpdateRequestDTO.builder()
                .categoryId(1L)
                .productName("싸디싼 스마트폰")
                .summary("이건 진짜 싸다")
                .price(1000000)
                .discountPercentage(10)
                .goalAmount(1000000000)
                .contentImage("contentImage.jpg")
                .images(List.of(
                        ImageRegisterRequestDTO.builder()
                                .url("변경된image1.jpg")
                                .imageNumber(1)
                                .name("이미지1")
                                .build(),
                        ImageRegisterRequestDTO.builder()
                                .url("변경된image2.jpg")
                                .imageNumber(2)
                                .name("이미지2")
                                .build()
                ))
                .essentialDocuments(List.of(
                        DocumentRegisterRequestDTO.builder()
                                .url("변경된document1.pdf")
                                .docType(DocumentType.개발)
                                .name("필수문서1")
                                .build(),
                        DocumentRegisterRequestDTO.builder()
                                .url("변경된document2.pdf")
                                .docType(DocumentType.프로젝트)
                                .name("필수문서2")
                                .build()
                ))
                .build();

        projectService.updateProject(projectId, requestDTO);
    }

    @Test
    public void testFindProjectList() {
        ProjectListRequestDTO requestDTO = ProjectListRequestDTO.builder()
                .statusNumber(1)
                .build();

        List<ProjectListResponseDTO> projectList = projectService.getProjectList(requestDTO, PageRequestDTO.builder().page(1).size(2).build()).getDataList();
        log.info(projectList);
    }

    @Test
    public void testFindMainProjects() {
        ProjectMainResponseDTO projectMainResponseDTO = projectService.getMainProjects();
        log.info(projectMainResponseDTO);
    }
}
