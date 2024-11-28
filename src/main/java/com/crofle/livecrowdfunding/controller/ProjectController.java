package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.dto.request.*;
import com.crofle.livecrowdfunding.dto.response.*;
import com.crofle.livecrowdfunding.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
@Log4j2
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailResponseDTO> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectForUser(id));
    }

    @PostMapping
    public ResponseEntity<Void> createProject(@RequestBody ProjectRegisterRequestDTO requestDTO) {
        projectService.createProject(requestDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{id}/status/")
    public ResponseEntity<Void> updateProjectStatus(@PathVariable Long id, @RequestBody ProjectStatusRequestDTO requestDTO) {
        projectService.updateProjectStatus(id, requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/update")
    public ResponseEntity<ProjectDetailToUpdateResponseDTO> getProjectToUpdate(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectForManagerUpdate(id));
    }

    @GetMapping("/{id}/maker")
    public ResponseEntity<ProjectDetailForMakerResponseDTO> getProjectForMaker(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectForMaker(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateRequestDTO requestDTO) {
        projectService.updateProject(id, requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<PageListResponseDTO<ProjectListResponseDTO>> getProjectList(@RequestBody ProjectListRequestDTO requestDTO, @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(projectService.getProjectList(requestDTO, pageRequestDTO));
    }

    //프로젝트 등록하기
  //  @PostMapping(value="/project/projectRegist", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping(value="/projectRegist")
    public ResponseEntity<JSONObject> projectRegist(

            @RequestBody String jsonBody) throws Exception{
//            @RequestParam("makerId") Long makerId,
//            @RequestParam("planId") Long planId,
//            @RequestParam("categoryId") Long categoryId,
//            @RequestParam("productName") String productName,
//            @RequestParam("summary") String summary,
//            @RequestParam("price") Integer price,
//            @RequestParam("discountPercentage") Integer discountPercentage,
//            @RequestParam("goalAmount") Integer goalAmount,
//            @RequestParam("contentImage") List<MultipartFile> contentImage
//    )
//    {
//        log.info("checking yejina");
//        log.info(contentImage);

//        try {
//            // 프로젝트 객체 생성
//            Project project = Project.builder()
//                    .maker(makerId)
//                    .planId(planId)
//                    .categoryId(categoryId)
//                    .productName(productName)
//                    .summary(summary)
//                    .price(price)
//                    .discountPercentage(discountPercentage)
//                    .goalAmount(goalAmount)
//                    .reviewStatus("검토중")
//                    .build();
//
//            // 이미지 처리
//            if (!contentImage.isEmpty()) {
//                String imageUrl = fileService.saveFile(contentImage);  // 이미지 저장 로직
//                project.setContentImage(imageUrl);
//            }
//
//            // DB에 저장
//            projectService.registerProject(project);
//
//            JSONObject response = new JSONObject();
//            response.put("status", "success");
//            response.put("message", "프로젝트가 성공적으로 등록되었습니다.");
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            JSONObject errorResponse = new JSONObject();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", e.getMessage());
//
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
        return null;
    };

}


