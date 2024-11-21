package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.Liked;
import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.entity.User;
import com.crofle.livecrowdfunding.domain.id.LikedId;
import com.crofle.livecrowdfunding.dto.PageInfoDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ProjectLikedResponseDTO;
import com.crofle.livecrowdfunding.dto.request.LikedRequestDTO;
import com.crofle.livecrowdfunding.repository.LikedRepository;
import com.crofle.livecrowdfunding.repository.ProjectRepository;
import com.crofle.livecrowdfunding.repository.UserRepository;
import com.crofle.livecrowdfunding.service.LikedService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class LikedServiceImpl implements LikedService {
    private final LikedRepository likedRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(LikedRequestDTO request) {
        validateRequest(request);

        LikedId likedId = createLikedId(request);

        return likedRepository.existsById(likedId)
                ? handleUnlike(likedId)
                : handleLike(request, likedId);
    }

    @Transactional
    @Override
    public PageListResponseDTO<ProjectLikedResponseDTO> getUserLikedProjects(Long userId, PageRequestDTO pageRequestDTO) {
        Page<Project> likedProjects = likedRepository.findByUser(userId, pageRequestDTO.getPageable());
        return PageListResponseDTO.<ProjectLikedResponseDTO>builder()
                .dataList(likedProjects.stream()
                        .map(project -> ProjectLikedResponseDTO.builder()
                                .id(project.getId())
                                .productName(project.getProductName())
                                .description(project.getSummary())
                                .thumbnailUrl(project.getImages().get(0).getUrl())
                                .price(project.getPrice())
                                .percentage(project.getDiscountPercentage())
                                .build())
                        .collect(Collectors.toList()))
                .pageInfoDTO(PageInfoDTO.withAll()
                        .pageRequestDTO(pageRequestDTO)
                        .total((int)likedProjects.getTotalElements())
                        .build())
                .build();
//        return likedProjects.stream()
//                .map(project -> ProjectLikedResponseDTO.builder()
//                        .id(project.getId())
//                        .productName(project.getProductName())
//                        .description(project.getSummary())
//                        .thumbnailUrl(project.getImages().get(0).getUrl())
//                        .price(project.getPrice())
//                        .percentage(project.getDiscountPercentage())
//                        .build())
//                .collect(Collectors.toList());
    }

    private void validateRequest(LikedRequestDTO request) {
        if (request.getProjectId() == null || request.getUserId() == null) {
            throw new IllegalArgumentException("프로젝트 ID와 사용자 ID는 필수값입니다.");
        }
    }

    private LikedId createLikedId(LikedRequestDTO request) {
        return new LikedId(request.getUserId(), request.getProjectId());
    }

    private boolean handleUnlike(LikedId likedId) {
        likedRepository.deleteById(likedId);
        return false;
    }

    private boolean handleLike(LikedRequestDTO request, LikedId likedId) {
        Project project = findProject(request.getProjectId());
        User user = findUser(request.getUserId());

        Liked liked = createLiked(likedId, project, user);
        likedRepository.save(liked);

        return true;
    }

    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트를 찾을 수 없습니다. ID: " + projectId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    private Liked createLiked(LikedId likedId, Project project, User user) {
        return Liked.builder()
                .id(likedId)
                .project(project)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
