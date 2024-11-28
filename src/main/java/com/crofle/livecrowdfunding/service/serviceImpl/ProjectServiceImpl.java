package com.crofle.livecrowdfunding.service.serviceImpl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.crofle.livecrowdfunding.domain.entity.*;
import com.crofle.livecrowdfunding.domain.enums.DocumentType;
import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import com.crofle.livecrowdfunding.dto.PageInfoDTO;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.*;
import com.crofle.livecrowdfunding.repository.CategoryRepository;
import com.crofle.livecrowdfunding.repository.MakerRepository;
import com.crofle.livecrowdfunding.repository.ProjectRepository;
import com.crofle.livecrowdfunding.repository.RatePlanRepository;
import com.crofle.livecrowdfunding.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

    @Value("${ncp.storage.endpoint}")
    private String endPoint;

    @Value("${ncp.storage.region}")
    private String region;

    @Value("${ncp.storage.access-key}")
    private String accessKey;

    @Value("${ncp.storage.secret-key}")
    private String secretKey;

    @Value("${ncp.storage.bucket}")
    private String bucket;

    private AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponseDTO getProjectForUser(Long id, Long userId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 조회에 실패했습니다"));

        ProjectDetailResponseDTO projectDetailResponseDTO = modelMapper.map(project, ProjectDetailResponseDTO.class);
        projectDetailResponseDTO.setMaker(project.getMaker().getName());
        projectDetailResponseDTO.setCategory(project.getCategory().getClassification());
        //우선 같이 가져오지만 비동기 처리 고려
        projectDetailResponseDTO.setLikeCount(project.getLikes().size());
        projectDetailResponseDTO.setIsLiked(project.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userId)));

        return projectDetailResponseDTO;
    }

    @Transactional
    @Override
    public void createProject(ProjectRegisterRequestDTO requestDTO,
                              List<MultipartFile> images,
                              List<MultipartFile> documents) {
        Maker maker = makerRepository.findById(requestDTO.getMakerId())
                .orElseThrow(() -> new EntityNotFoundException("메이커 조회에 실패했습니다"));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리 조회에 실패했습니다"));

        RatePlan ratePlan = ratePlanRepository.findById(requestDTO.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("요금제 조회에 실패했습니다"));

        String contentImageUrl = uploadToNcp(requestDTO.getContentImage(), "content-images/");

        Project project = Project.builder()
                .maker(maker)
                .ratePlan(ratePlan)
                .category(category)
                .productName(requestDTO.getProductName())
                .summary(requestDTO.getSummary())
                .price(requestDTO.getPrice())
                .discountPercentage(requestDTO.getDiscountPercentage())
                .goalAmount(requestDTO.getGoalAmount())
                .contentImage(contentImageUrl)
                .build();

        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                String imageUrl = uploadToNcp(images.get(i), "images/");
                project.getImages().add(Image.builder()
                        .project(project)
                        .url(imageUrl)
                        .imageNumber(i)
                        .name(images.get(i).getOriginalFilename())
                        .build());
            }
        }

        if (documents != null) {
            for (int i = 0; i < documents.size(); i++) {
                String documentUrl = uploadToNcp(documents.get(i), "documents/");
                project.getEssentialDocuments().add(EssentialDocument.builder()
                        .project(project)
                        .url(documentUrl)
                        .name(documents.get(i).getOriginalFilename())
                        .docType(DocumentType.valueOf(documents.get(i).getContentType()))
                        .build());
            }
        }

        projectRepository.save(project);
    }

    private String uploadToNcp(MultipartFile file, String folderName) {
        AmazonS3 s3 = getS3Client();
        String fileName = folderName + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3.putObject(new PutObjectRequest(bucket, fileName,
                    file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return String.format("https://%s.kr.object.ncloudstorage.com/%s", bucket, fileName);

        } catch (IOException | AmazonS3Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
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
        projectDetailForMakerResponseDTO.setCurrentSales((int) project.getOrders().stream()
                .filter(order -> order.getPaymentHistory() != null)
                .mapToInt(order -> order.getPaymentPrice())
                .sum());
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

    @Transactional(readOnly = true)
    @Override   // 좋지못한 로직이지만 erd 를 바꿔야해서 리팩토링으로 남겨둬야 함 1. 검토중, 반려 2. 승인& 펀딩중 3. 성공, 미달성
    public PageListResponseDTO<ProjectListResponseDTO> getProjectList(Long makerId, int statusNumber, PageRequestDTO pageRequestDTO) {;

        Page<ProjectListResponseDTO> projectListResponseDTOS;

        switch (statusNumber) {
            case 1:
                projectListResponseDTOS = projectRepository.findByReviewStatuses(makerId, List.of(ProjectStatus.검토중, ProjectStatus.반려), pageRequestDTO.getPageable());
                return PageListResponseDTO.<ProjectListResponseDTO>builder()
                        .dataList(projectListResponseDTOS.getContent())
                        .pageInfoDTO(PageInfoDTO.withAll()
                                .pageRequestDTO(pageRequestDTO)
                                .total((int) projectListResponseDTOS.getTotalElements())
                                .build())
                        .build();

            case 2:
                projectListResponseDTOS = projectRepository.findByReviewStatusAndProgressStatus(makerId, ProjectStatus.승인, ProjectStatus.펀딩중, pageRequestDTO.getPageable());
                return getProjectListResponseDTOPageListResponseDTO(pageRequestDTO, projectListResponseDTOS);

            case 3:
                projectListResponseDTOS = projectRepository.findByProgressStatuses(makerId, List.of(ProjectStatus.성공, ProjectStatus.미달성), pageRequestDTO.getPageable());
                return getProjectListResponseDTOPageListResponseDTO(pageRequestDTO, projectListResponseDTOS);
            default:
                //not allowed
                return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectMainResponseDTO getMainProjects() {
        List<LiveFundingInMainResponseDTO> liveFundingProjects = projectRepository.findLiveFundingInMain();
        List<TopFundingInMainResponseDTO> topFundingProjects = projectRepository.findTopFundingInMain(LocalDate.now().atStartOfDay());

        return ProjectMainResponseDTO.builder()
                .liveFundingProjects(liveFundingProjects)
                .topFundingProjects(topFundingProjects)
                .build();
    }

    @Override
    public List<ProjectLiveVODResponseDTO> getLiveAndVODProjectList() {
        return projectRepository.findLiveAndVODProjectList();
    }

    @Override
    public PageListResponseDTO<ProjectWithConditionResponseDTO> getCategoryProjects(Long categoryId, PageRequestDTO pageRequestDTO) {
        Page<ProjectWithConditionResponseDTO> projectPage = projectRepository.findByCategoryIdProject(categoryId, pageRequestDTO.getPageable());
        return PageListResponseDTO.<ProjectWithConditionResponseDTO>builder()
                .dataList(projectPage.getContent())
                .pageInfoDTO(PageInfoDTO.withAll()
                        .pageRequestDTO(pageRequestDTO)
                        .total((int) projectPage.getTotalElements())
                        .build())
                .build();
    }

    @Override
    public PageListResponseDTO<ProjectWithConditionResponseDTO> getSearchProjects(String keyword, PageRequestDTO pageRequestDTO) {
        Page<ProjectWithConditionResponseDTO> projectPage = projectRepository.findByKeywordProject(keyword, pageRequestDTO.getPageable());
        return PageListResponseDTO.<ProjectWithConditionResponseDTO>builder()
                .dataList(projectPage.getContent())
                .pageInfoDTO(PageInfoDTO.withAll()
                        .pageRequestDTO(pageRequestDTO)
                        .total((int) projectPage.getTotalElements())
                        .build())
                .build();
    }


    private PageListResponseDTO<ProjectListResponseDTO> getProjectListResponseDTOPageListResponseDTO(PageRequestDTO pageRequestDTO, Page<ProjectListResponseDTO> projects) {
        return PageListResponseDTO.<ProjectListResponseDTO>builder()
                .dataList(projects.getContent())
                .pageInfoDTO(PageInfoDTO.withAll()
                        .pageRequestDTO(pageRequestDTO)
                        .total((int) projects.getTotalElements())
                        .build())
                .build();
    }
}