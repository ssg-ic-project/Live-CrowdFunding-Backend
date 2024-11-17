package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.*;
import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import com.crofle.livecrowdfunding.dto.request.ProjectRegisterRequestDTO;
import com.crofle.livecrowdfunding.dto.request.ProjectStatusRequestDTO;
import com.crofle.livecrowdfunding.dto.request.ProjectUpdateRequestDTO;
import com.crofle.livecrowdfunding.dto.response.*;
import com.crofle.livecrowdfunding.repository.*;
import com.crofle.livecrowdfunding.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MakerRepository makerRepository;
    private final RatePlanRepository ratePlanRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public ProjectDetailResponseDTO getProjectForUser(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 조회에 실패했습니다"));

        ProjectDetailResponseDTO projectDetailResponseDTO = modelMapper.map(project, ProjectDetailResponseDTO.class);
        projectDetailResponseDTO.setMaker(project.getMaker().getName());
        projectDetailResponseDTO.setCategory(project.getCategory().getClassification());
        //우선 같이 가져오지만 비동기 처리 고려
        projectDetailResponseDTO.setLikeCount(project.getLikes().size());

        return projectDetailResponseDTO;
    }

    @Transactional
    @Override
    public void createProject(ProjectRegisterRequestDTO requestDTO) {
        Maker maker = makerRepository.findById(requestDTO.getMakerId())
                .orElseThrow(() -> new EntityNotFoundException("메이커 조회에 실패했습니다"));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리 조회에 실패했습니다"));

        RatePlan ratePlan = ratePlanRepository.findById(requestDTO.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("요금제 조회에 실패했습니다"));

        Project project = Project.builder()
                .maker(maker)
                .ratePlan(ratePlan)
                .category(category)
                .productName(requestDTO.getProductName())
                .summary(requestDTO.getSummary())
                .price(requestDTO.getPrice())
                .discountPercentage(requestDTO.getDiscountPercentage())
                .goalAmount(requestDTO.getGoalAmount())
                .contentImage(requestDTO.getContentImage())
                .build();

        if(requestDTO.getImages() != null) {
            requestDTO.getImages().forEach(image -> {
                project.getImages().add(Image.builder()
                        .project(project)
                        .url(image.getUrl())
                        .imageNumber(image.getImageNumber())
                        .name(image.getName())
                        .build());
            });
        }

        if(requestDTO.getEssentialDocuments() != null) {
            requestDTO.getEssentialDocuments().forEach(document -> {
                project.getEssentialDocuments().add(EssentialDocument.builder()
                        .project(project)
                        .name(document.getName())
                        .url(document.getUrl())
                        .docType(document.getDocType())
                        .build());
            });
        }


        projectRepository.save(project);
    }

    @Transactional
    @Override
    public void updateProjectStatus(Long id, ProjectStatusRequestDTO requestDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 조회에 실패했습니다"));

        project.setReviewProjectStatus(requestDTO.getStatus());
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectDetailForMakerResponseDTO getProjectForMaker(Long id) {

        Project project = projectRepository.findByIdWithOrders(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 조회에 실패했습니다"));

        Project projectWithImages = projectRepository.findByIdWithImages(id).orElseThrow();
        Project projectWithDocs = projectRepository.findByIdWithDocuments(id).orElseThrow();

        ProjectDetailForMakerResponseDTO projectDetailForMakerResponseDTO = modelMapper.map(project, ProjectDetailForMakerResponseDTO.class);
        projectDetailForMakerResponseDTO.setCategory(project.getCategory().getClassification());
        projectDetailForMakerResponseDTO.setShowStatus(checkShowStatus(project));
        projectDetailForMakerResponseDTO.setPaymentCount((int) project.getOrders().stream()
                .filter(order -> order.getPaymentHistory() != null)
                .count());

        projectDetailForMakerResponseDTO.setImages(
                projectWithImages.getImages().stream()
                        .map(image -> modelMapper.map(image, ImageResponseDTO.class))
                        .collect(Collectors.toList())
        );

        projectDetailForMakerResponseDTO.setEssentialDocuments(
                projectWithDocs.getEssentialDocuments().stream()
                        .map(document -> modelMapper.map(document, DocumentResponseDTO.class))
                        .collect(Collectors.toList())
        );

        return projectDetailForMakerResponseDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectDetailToUpdateResponseDTO getProjectForManagerUpdate(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 조회에 실패했습니다"));

        ProjectDetailToUpdateResponseDTO projectDetailToUpdateResponseDTO = modelMapper.map(project, ProjectDetailToUpdateResponseDTO.class);
        projectDetailToUpdateResponseDTO.setCategory(project.getCategory().getClassification());
        return projectDetailToUpdateResponseDTO;
    }

    @Transactional
    @Override
    public void updateProject(Long id, ProjectUpdateRequestDTO requestDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 조회에 실패했습니다"));

        project.setCategory(categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리 조회에 실패했습니다")));



        project.setProductName(requestDTO.getProductName());
        project.setSummary(requestDTO.getSummary());
        project.setPrice(requestDTO.getPrice());
        project.setDiscountPercentage(requestDTO.getDiscountPercentage());
        project.setGoalAmount(requestDTO.getGoalAmount());
        project.setContentImage(requestDTO.getContentImage());

        if(requestDTO.getImages() != null) {
            project.getImages().clear();
            requestDTO.getImages().forEach(image -> {
                project.getImages().add(Image.builder()
                        .project(project)
                        .url(image.getUrl())
                        .imageNumber(image.getImageNumber())
                        .name(image.getName())
                        .build());
            });
        }

        if(requestDTO.getEssentialDocuments() != null) {
            project.getEssentialDocuments().clear();
            requestDTO.getEssentialDocuments().forEach(document -> {
                project.getEssentialDocuments().add(EssentialDocument.builder()
                        .project(project)
                        .name(document.getName())
                        .url(document.getUrl())
                        .docType(document.getDocType())
                        .build());
            });
        }
    }


    private String checkShowStatus(Project project) {
        if(project.getReviewProjectStatus() == ProjectStatus.승인) {
            return project.getProgressProjectStatus().toString();
        }
        return project.getReviewProjectStatus().toString();
    }
}