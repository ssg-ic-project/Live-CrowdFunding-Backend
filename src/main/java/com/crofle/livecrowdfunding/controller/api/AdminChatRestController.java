package com.crofle.livecrowdfunding.controller.api;

import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.request.UserStatusRequestDTO;
import com.crofle.livecrowdfunding.dto.response.ChatReportDetailResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ChatReportListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.service.AdminChatReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminChatRestController {
    private final AdminChatReportService adminChatReportService;

    //신고 목록 조회
    @GetMapping("/chat-reports")
    public ResponseEntity<PageListResponseDTO<ChatReportListResponseDTO>> getReportList( //조건 조회 없음
                                                                                         @RequestParam(defaultValue = "1") int page) { //page 꼭 넣어주어함
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .build();


        return ResponseEntity.ok(adminChatReportService.findReportList(pageRequestDTO));
    }

    //신고 상세 정보 조회 모달
    @GetMapping("/{reportId}")
    public ResponseEntity<ChatReportDetailResponseDTO> getReportDetail(
        @PathVariable Long reportId){
        return ResponseEntity.ok(adminChatReportService.findReportDetail(reportId));
    }

    //사용자 상태 변경
    @PostMapping("/{reportId}/user-status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable Long reportId,
            @RequestBody UserStatusRequestDTO updateDTO) {
        // reportId를 통해 userId 조회
        log.info("updateDTO 확인용, yejin: ", updateDTO);
        Long userId = adminChatReportService.getUserIdByReportId(reportId);



        // 조회한 userId를 updateDTO에 설정
        updateDTO.setUserId(userId);
        updateDTO.setReportId(reportId);
        log.info("check delete? ", updateDTO.isDeleteReport());

        adminChatReportService.updateUserStatus(updateDTO);
        return ResponseEntity.ok("사용자 상태가 변경되었습니다.");
    }

}