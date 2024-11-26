package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.domain.entity.ChatReport;
import com.crofle.livecrowdfunding.domain.entity.Manager;
import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.entity.User;
import com.crofle.livecrowdfunding.dto.request.CreateChatReportRequest;
import com.crofle.livecrowdfunding.dto.response.ReportResponseDTO;
import com.crofle.livecrowdfunding.repository.ChatReportRepository;
import com.crofle.livecrowdfunding.repository.ProjectRepository;
import com.crofle.livecrowdfunding.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatReportService {
    private final ChatReportRepository chatReportRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public ReportResponseDTO createReport(CreateChatReportRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        Project project = projectRepository.findById(request.getProjectId()).orElseThrow();
        Manager manager = project.getManager();

        ChatReport report = ChatReport.builder()
                .user(user)
                .project(project)
                .manager(manager)
                .reason(request.getReason())
                .chatMessage(request.getChatMessage())
                .createdAt(LocalDateTime.now())
                .build();

        chatReportRepository.save(report);

        ReportResponseDTO reportResponseDTO = ReportResponseDTO.builder()
                .message("신고가 정상적으로 접수되었습니다.")
                .status(200)
                .build();

        return reportResponseDTO;
    }
}

