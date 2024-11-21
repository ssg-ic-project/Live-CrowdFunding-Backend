package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.ChatReport;
import com.crofle.livecrowdfunding.domain.entity.User;
import com.crofle.livecrowdfunding.domain.enums.UserStatus;
import com.crofle.livecrowdfunding.dto.PageInfoDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.request.UserStatusRequestDTO;
import com.crofle.livecrowdfunding.dto.response.ChatReportDetailResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ChatReportListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.repository.ChatReportRepository;
import com.crofle.livecrowdfunding.repository.UserRepository;
import com.crofle.livecrowdfunding.service.AdminChatReportService;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminChatReportServiceImpl implements AdminChatReportService {
    private final ChatReportRepository chatReportRepository;
    private final UserRepository userRepository;

    @Autowired
    private EmailService emailService;

//    @Qualifier("ChatmodelMapper")// Bean 이름 지정
//    private final ModelMapper modelMapper;
    private final ModelMapper modelMapper;
    
    @Override //신고 list 조회
    public PageListResponseDTO<ChatReportListResponseDTO> findReportList(PageRequestDTO pageRequestDTO) {
        //Pageable로 만들기
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage()-1, pageRequestDTO.getSize());

        //Fetch paginated result using repository
        Page<ChatReport> chatReports = chatReportRepository.findAll(pageable);

        //ChatReport entity랑 ChatReportListDTO랑 매핑하기
//        List<ChatReportListDTO> chatReportListDTOList = chatReports.stream()
//                .map(chat -> modelMapper.map(chat, ChatReportListDTO.class))
//                .collect(Collectors.toList());

        //project, user의 모든 정보가 필요없을 때:
        List<ChatReportListResponseDTO> chatReportListDTOListResponse = chatReports.stream()
                .map(chat -> ChatReportListResponseDTO.builder()
                        .id(chat.getId())
                        .userId(chat.getUser().getId())
                        .projectId(chat.getProject().getId())
                        .managerId(chat.getManager().getId())
                        .reason(chat.getReason())
                        .chatMessage(chat.getChatMessage())
                        .createdAt(chat.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        //PageListResponseDTO로 만들어주기
        PageInfoDTO pageInfoDTO = PageInfoDTO.withAll().pageRequestDTO(pageRequestDTO).total((int) chatReports.getTotalElements()).build();

        PageListResponseDTO<ChatReportListResponseDTO> pageListResponseDTO = PageListResponseDTO.<ChatReportListResponseDTO>builder()
                .pageInfoDTO(pageInfoDTO)
                .dataList(chatReportListDTOListResponse)
                .build();

        return pageListResponseDTO;
    }

    @Override
    public ChatReportDetailResponseDTO findReportDetail(Long reportId) {
        ChatReport chatReport = chatReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고 내역이 없습니다"));

        return ChatReportDetailResponseDTO.builder()
                .id(chatReport.getId())
                .userId(chatReport.getUser().getId())
                .userStatus(chatReport.getUser().getStatus())
                .projectId(chatReport.getProject().getId())
                .projectName(chatReport.getProject().getProductName())
                .managerId(chatReport.getManager().getId())
                .reason(chatReport.getReason())
                .chatMessage(chatReport.getChatMessage())
                .createdAt(chatReport.getCreatedAt())
                .build();
    }

    // 사용자 상태 변경
    @Override
    @Transactional
    public void updateUserStatus(UserStatusRequestDTO updateDTO) {
        User user = userRepository.findById(updateDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 없습니다."));

        // 2. 신고 내역 삭제 (요청된 경우)
        if (updateDTO.isDeleteReport()) {
            ChatReport chatReport = chatReportRepository.findById(updateDTO.getReportId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 신고 내역이 없습니다"));
            chatReportRepository.delete(chatReport);
            return;
        }
        //DB 업데이트
        switch (updateDTO.getStatus()) {
            case 활성화 -> user.activateUser();
            case 비활성화 -> user.deactivateUser();
            case 정지 -> user.suspendUser();
        }
        //이메일 전송 (사용자 이메일, 실시간 방송 이름, 직원 코멘트, 활성화일경우 1달 X사용, 정지는 평생)
        try{
            String duration = updateDTO.getStatus() == UserStatus.정지 ? "영구적으로": "1 개월동안";
            sendNotificationEmail(user.getEmail(), updateDTO.getStatus(), duration);
        }catch(Exception e){
            log.error("이메일 발송 실패: " + e.getMessage());
        }

    }
    private void sendNotificationEmail(String email, UserStatus status, String duration){
        //필요하면 나중에 어느 방송에서 어떤 채팅으로 인해 제한이 되었는지 명시.
        String subject = "[알림] 실시간 방송 이용 제한 안내";
        String message = String.format(
                "안녕하세요.\n\n" +
                "회원님의 계정이 %s 상태로 변경되어 실시간 방송 이용이 %s 제한됩니다.\n\n" +
                "사유: %s\n\n" +
                "추가 문의사항이 있으시다면 고객센터로 연락 부탁드립니다.\n\n" +
                 "고객센터 연락처: 02-2882-0832\n" +
                 "대표번호: 1234-5678\n" +
                 "운영시간: 평일 09:00-18:00 (공휴일 제외)\n",
                status, duration, status == UserStatus.정지 ? "운영정책 위반" : "운영정책 미준수"
        );
        emailService.sendEmail(email, subject, message);
        log.info("이메일 전송 완료");
    }



    // reportId를 통해 userId 조회
    public Long getUserIdByReportId(Long reportId) {
        ChatReport chatReport = chatReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reportId: " + reportId));
        return chatReport.getUser().getId();
    }
}