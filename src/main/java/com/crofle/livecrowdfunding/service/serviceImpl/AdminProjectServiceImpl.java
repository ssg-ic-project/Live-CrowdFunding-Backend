package com.crofle.livecrowdfunding.service.serviceImpl;
import com.crofle.livecrowdfunding.domain.entity.Image;
import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import com.crofle.livecrowdfunding.dto.*;
import com.crofle.livecrowdfunding.dto.request.ProjectApprovalRequestDTO;
import com.crofle.livecrowdfunding.dto.response.EssentialDocumentDTO;
import com.crofle.livecrowdfunding.dto.response.ProjectResponseInfoDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class AdminProjectServiceImpl implements AdminProjectService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Override
    public PageListResponseDTO<ProjectResponseInfoDTO> findProjectList(PageRequestDTO pageRequestDTO) { //naming precision required
        // 1. Create a Pageable object using the pageRequestDTO
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());

        // 2. Fetch a paginated result using the repository
        //반환값을 entity 형태로 저장
        Page<Project> projectPage = projectRepository.findAll(pageable);

        // 3. Map the Project entities to ProjectInfoDTO objects
        //entity 형태를 dto 형태로 response할 수 있는 형태로 변환
        List<ProjectResponseInfoDTO> projectResponseInfoDTOList = projectPage.stream()
                    .map(project -> modelMapper.map(project, ProjectResponseInfoDTO.class))
                    .collect(Collectors.toList());

        // 4. Create PageInfoDTO from pageRequestDTO and the total count of projects
        PageInfoDTO pageInfoDTO = PageInfoDTO.withAll().pageRequestDTO(pageRequestDTO).total((int) projectPage.getTotalElements())
                .build();

        // 5. Return the response wrapped in a PageListResponseDTO
        //여기에서 넣어줘야 하는 pageInfoDTO를 만들기 위해 위에서 build를 해준다.
        PageListResponseDTO<ProjectResponseInfoDTO> pageListResponseDT = PageListResponseDTO.<ProjectResponseInfoDTO>builder()
                .pageInfoDTO(pageInfoDTO)
                .dataList(projectResponseInfoDTOList)
                .build();

        //Service 단에서 체크하는 용
        for (ProjectResponseInfoDTO p : pageListResponseDT.getDataList()) {
            log.info("Project ID: " + p.getId());
            log.info("Product Name: " + p.getProductName());
        }
        return pageListResponseDT;
    }

    @Transactional(readOnly = true) //없애도 잘 동작함..와이?
    @Override
    public ProjectResponseInfoDTO findProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 조회에 실패했습니다"));

        log.info("check1: ", project.getManager().getId());
        ProjectResponseInfoDTO projectResponseInfoDTO = modelMapper.map(project, ProjectResponseInfoDTO.class);
        return projectResponseInfoDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EssentialDocumentDTO> findEssentialDocs(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 프로젝트는 존재하지 않습니다"));

        //front에서 linking할때 doc 별로 보여주는 란 지정하기
         List<EssentialDocumentDTO> documents = project.getEssentialDocuments().stream()
                .map(doc -> modelMapper.map(doc, EssentialDocumentDTO.class))
                .collect(Collectors.toList());

         if(!project.getImages().isEmpty()) {
             Image firstImage = project.getImages().stream()
                     .min(Comparator.comparing(Image::getImageNumber))
                     .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다"));
             EssentialDocumentDTO imageDoc = new EssentialDocumentDTO();
             imageDoc.setUrl(firstImage.getUrl()); //string 타입의 image가지고 오기
             documents.add(imageDoc);
         }
         return documents;
    }

    //승인, 반려, 반려 사유 DB 업데이트하기 + 이메일 전송하기
    @Override
    @Transactional
    public void updateApprovalStatus(Long id, ProjectApprovalRequestDTO request) {
        //1. 프로젝트 존재 유무 확인
        Project project = projectRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 프로젝트는 존재하지 않습니다"));

        //2. 상태 업데이트
        project.setReviewProjectStatus(request.getStatus());

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