package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.domain.id.LikedId;
import com.crofle.livecrowdfunding.dto.request.LikedRequestDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class LikedServiceTest {
    @Autowired
    private LikedService likedService;

    @Test
    public void testToggleLike() {
        Long projectId = 1L;
        Long userId = 1L;
        LikedRequestDTO likedRequestDTO = LikedRequestDTO.builder()
                .projectId(projectId)
                .userId(userId)
                .build();
        likedService.toggleLike(likedRequestDTO);
    }

    @Test
    public void testGetLikedProjects() {
        Long userId = 1L;

//        likedService.getUserLikedProjects(userId).stream()
//                .forEach(project -> log.info(project));
    }
}
