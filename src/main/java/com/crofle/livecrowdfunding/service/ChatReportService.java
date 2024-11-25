//package com.crofle.livecrowdfunding.service;
//
//import com.crofle.livecrowdfunding.domain.entity.ChatReport;
//import com.crofle.livecrowdfunding.domain.entity.Project;
//import com.crofle.livecrowdfunding.domain.entity.User;
//import com.crofle.livecrowdfunding.dto.ChatReportDTO;
//import com.crofle.livecrowdfunding.dto.request.CreateChatReportRequest;
//import com.crofle.livecrowdfunding.dto.request.UserInfoRequestDTO;
//import com.crofle.livecrowdfunding.dto.response.UserInfoResponseDTO;
//import com.crofle.livecrowdfunding.repository.ChatReportRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ChatReportService {
//    private final ChatReportRepository chatReportRepository;
//    private final UserService userService;
//    private final ProjectService projectService;
//
//    @Transactional
//    public ChatReportDTO createReport(CreateChatReportRequest request, Long reporterUserId) {
//        UserInfoResponseDTO reporter = userService.findUser(reporterUserId);
////        Project project = projectService.getProjectForUser(request.getProjectId());
//
//        ChatReport report = ChatReport.builder()
////                .user(reporter)
////                .project(project)
////                .manager(project.getManager())
//                .reason(request.getReason())
//                .chatMessage(request.getChatMessage())
//                .build();
//
//        return ChatReportDTO.from(chatReportRepository.save(report));
//    }
//}

