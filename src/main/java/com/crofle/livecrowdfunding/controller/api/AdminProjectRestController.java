package com.crofle.livecrowdfunding.controller.api;

import com.crofle.livecrowdfunding.dto.SearchTypeDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.request.ProjectApprovalRequestDTO;
import com.crofle.livecrowdfunding.dto.response.EssentialDocumentDTO;
import com.crofle.livecrowdfunding.dto.response.ImageResponseDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ProjectResponseInfoDTO;
import com.crofle.livecrowdfunding.service.AdminProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminProjectRestController {

    private final AdminProjectService adminProjectService;

    @GetMapping("/projects")
    public ResponseEntity<PageListResponseDTO<ProjectResponseInfoDTO>> getProjectList(
            @RequestParam (defaultValue = "1") int page,
            @RequestParam(required = false) String RS,
            @RequestParam(required = false) String PS,
            @RequestParam(required = false) String SD,
            @RequestParam(required = false) String ED,
            @RequestParam(required = false) String projname) {

//        int pageNumber = (string(page) != null) ? page : 1;

        log.info("yejin project page check");

        SearchTypeDTO searchTypeDTO = SearchTypeDTO.builder()
                .RS(RS)
                .PS(PS)
                .SD(SD)
                .ED(ED)
                .build();
        log.info("check asked page with filter yejin: ");
        log.info(RS);
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .search(searchTypeDTO)
                .projectName(projname)
                .build();

        PageListResponseDTO<ProjectResponseInfoDTO> data = adminProjectService.findProjectList(pageRequestDTO);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    //추가 이미지들 가지고 오기 + Thumbnail 포함
    @GetMapping("/project/{id}/images")
    public ResponseEntity<List<ImageResponseDTO>>getImages(@PathVariable Long id){
        log.info("checking: ", id);
        return ResponseEntity.ok(adminProjectService.findProjectImages(id));
    }

    //승인, 반려를 위한 프로젝트별 상세 조회 페이지에서 필요한 서류들 보기. 프론트에서 해당 서류들 위치 지정해주기.
    @GetMapping("/project/{id}/docs")
    public ResponseEntity<List<EssentialDocumentDTO>> getProjectDocs(@PathVariable Long id){
        return ResponseEntity.ok(adminProjectService.findEssentialDocs(id));
    }

    //승인/반려 상태 변경 POST
    @PostMapping("/project/{id}/approval-status")
    public ResponseEntity<String> updateAppprStatus(
            @PathVariable Long id, //projectid를 뜻함
            @RequestBody ProjectApprovalRequestDTO request //상태랑, 반려시 사유작성
    ) {
        log.info("check from back: ", id, request);
        //내 스타일대로 exception처리 바꿔주기
        try {
            adminProjectService.updateApprovalStatus(id, request);
            return ResponseEntity.ok("승인 상태가 성공적으로 변경 완료. 사용자에게 리마인드 이메일 전송 완료");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("승인 상태 변경 중 오류가 발생했습니다.");
        }
    }

    //프로젝트 ID별 상세 내용 조회
    @GetMapping("/project/admin/{id}")
    public ResponseEntity<ProjectResponseInfoDTO> getProject(@PathVariable Long id) {
        ProjectResponseInfoDTO returns = adminProjectService.findProject(id);
        log.info(returns);
        return ResponseEntity.ok(returns);
    }

}