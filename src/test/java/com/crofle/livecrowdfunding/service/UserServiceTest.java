package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.UserInfoRequestDTO;
import com.crofle.livecrowdfunding.dto.response.UserInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void findUserTest() {
        Long userId = 1L;

        UserInfoResponseDTO userInfoResponseDTO = userService.findUser(userId);

        log.info(userInfoResponseDTO);
    }

    @Test
    public void updateUserTest() {
        Long id = 1L;

        UserInfoRequestDTO userInfoRequestDTO = UserInfoRequestDTO.builder()
                .name("홍길동1")
                .nickname("홍길동1")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .detailAddress("강남대로 123")
                .notification(true)
                .build();

        userService.updateUser(id, userInfoRequestDTO);
    }

    @Test
    public void deleteUserTest() {
        Long userId = 9L;

        userService.deleteUser(userId);
    }
}
