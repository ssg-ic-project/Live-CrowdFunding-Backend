package com.crofle.livecrowdfunding.service.serviceImpl;
import com.crofle.livecrowdfunding.domain.entity.Image;
import com.crofle.livecrowdfunding.domain.entity.Manager;
import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import com.crofle.livecrowdfunding.dto.*;
import com.crofle.livecrowdfunding.dto.request.ProjectApprovalRequestDTO;
import com.crofle.livecrowdfunding.dto.response.EssentialDocumentDTO;
import com.crofle.livecrowdfunding.dto.response.ImageResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ProjectResponseInfoDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.repository.ManagerRepository;
import com.crofle.livecrowdfunding.repository.ProjectRepository;
import com.crofle.livecrowdfunding.service.AdminProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class AdminProjectServiceImpl implements AdminProjectService {
    private final ProjectRepository projectRepository;
    private final ManagerRepository managerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Override
    public PageListResponseDTO<ProjectResponseInfoDTO> findProjectList(PageRequestDTO pageRequestDTO) { //naming precision required

        log.info("Current Page: " + pageRequestDTO.getPage());
        log.info("Page Size: " + pageRequestDTO.getSize());

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());

        // 검색 조건 처리
        SearchTypeDTO searchType = pageRequestDTO.getSearch();
        ProjectStatus reviewStatus = null;
        ProjectStatus progressStatus = null;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (searchType != null) {
            if (searchType.getRS() != null && !searchType.getRS().isEmpty()) {
                reviewStatus = ProjectStatus.valueOf(searchType.getRS());
            }
            if (searchType.getPS() != null && !searchType.getPS().isEmpty()) {
                progressStatus = ProjectStatus.valueOf(searchType.getPS());
            }
            if (searchType.getSD() != null && !searchType.getSD().isEmpty()) {
                startDate = LocalDateTime.parse(searchType.getSD() + "T00:00:00");
            }
            if (searchType.getED() != null && !searchType.getED().isEmpty()) {
                endDate = LocalDateTime.parse(searchType.getED() + "T23:59:59");
            }
        }

        // 검색 쿼리 실행
        Page<Project> projectPage = projectRepository.findBySearchConditions(
                reviewStatus,
                progressStatus,
                startDate,
                endDate,
                pageRequestDTO.getProjectName(),
                pageable
        );

        List<ProjectResponseInfoDTO> projectResponseInfoDTOList = projectPage.stream()
                .map(project -> modelMapper.map(project, ProjectResponseInfoDTO.class))
                .collect(Collectors.toList());

        PageInfoDTO pageInfoDTO = PageInfoDTO.withAll()
                .pageRequestDTO(pageRequestDTO)
                .total((int) projectPage.getTotalElements())
                .build();

        return PageListResponseDTO.<ProjectResponseInfoDTO>builder()
                .pageInfoDTO(pageInfoDTO)
                .dataList(projectResponseInfoDTOList)
                .build();
}

    @Transactional(readOnly = true) //없애도 잘 동작함..와이?
    @Override
    public ProjectResponseInfoDTO findProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 조회에 실패했습니다"));

        ProjectResponseInfoDTO projectResponseInfoDTO = modelMapper.map(project, ProjectResponseInfoDTO.class);
        return projectResponseInfoDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ImageResponseDTO>findProjectImages(Long id){
        Project project = projectRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 프로젝트는 존재하지 않습니다"));
        List<ImageResponseDTO> images = project.getImages().stream()
                .map(image ->modelMapper.map(image, ImageResponseDTO.class))
                .collect(Collectors.toList());

        return images;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EssentialDocumentDTO> findEssentialDocs(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 프로젝트는 존재하지 않습니다"));

        //front에서 linking할때 doc 별로 보여주는 란 지정하기
         List<EssentialDocumentDTO> documents = project.getEssentialDocuments().stream()
                .map(doc -> modelMapper.map(doc, EssentialDocumentDTO.class))
                .collect(Collectors.toList());

         return documents;
    }

    //승인, 반려, 반려 사유 DB 업데이트하기 + 이메일 전송하기
    @Override
    @Transactional
    public void updateApprovalStatus(Long id, ProjectApprovalRequestDTO request) {
        //1. 프로젝트 존재 유무 확인
        Project project = projectRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 프로젝트는 존재하지 않습니다"));

        Manager manager = managerRepository.findManager(request.getManagerId()).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 Manager 입니다"));

        //1.5. manager 저장하기
        project.setManager(manager);
        log.info("yejin from manager");
        log.info(request.getManagerId());

        //trigger과 충돌땜에 여기에 구현
        project.setStartAt(LocalDateTime.now());
        project.setEndAt(LocalDateTime.now().plusMonths(1));

        //2. 상태 업데이트
        project.setReviewProjectStatus(request.getStatus());

        project.setProgressProjectStatus(ProjectStatus.펀딩중);

        //3. 반려인 경우 사유 저장
        if(request.getStatus() == ProjectStatus.반려){
            project.setRejectionReason(request.getRejectionReason());
        }
        //4. DB에 저장
        projectRepository.save(project);

        //5. 이메일 발송
        try{
            sendNotificationEmail(project, request);
        }catch(Exception e) {
            log.error("이메일 발송 실패: " + e.getMessage());
        }
    }
    private void sendNotificationEmail(Project project, ProjectApprovalRequestDTO request){
        String subject;
        String content;

        if(request.getStatus() == ProjectStatus.반려){
            subject = "[펀딩] 프로젝트 심사 결과: 반려";
            content = String.format("""
                    안녕하세요, %s님
                    요청하신 프로젝트 '%s'가 다음과 같은 사유로 반려되었습니다:
                    %s
                    수정 후 다시 제출해 주시기 바랍니다.
                    """,
                    project.getMaker().getName(),
                    project.getProductName(),
                    request.getRejectionReason()
                    );
        }else{
            subject = "[펀딩] 프로젝트 심사 결과: 승인";
            content = String.format("""
                    안녕하세요, %s님
                    요청하신 프로젝트  '%s'가 승인되었습니다.
                    펀딩을 시작하실 수 있습니다.
                    """,
                    project.getMaker().getName(),
                    project.getProductName()
                    );
        }
        emailService.sendEmail(project.getMaker().getEmail(), subject, content);
    }
}