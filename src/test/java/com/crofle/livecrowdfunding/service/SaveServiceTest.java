package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.SaveMakerRequestDTO;
import com.crofle.livecrowdfunding.dto.request.SaveUserRequestDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Log4j2
public class SaveServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private MakerService makerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("사용자 저장 테스트")
    public void testUserSave() {
        SaveUserRequestDTO request = SaveUserRequestDTO.builder()
                .name("aaaaa")
                .nickname("bbbbb")
                .phone("000-000-0000")
                .password(passwordEncoder.encode("1234"))
                .gender(true)
                .birth("19900401")
                .email("dddd@ccc.com")
                .zipcode(12345)
                .address("테스트주소")
                .detailAddress("상세주소")
                .loginMethod(false)
                .notification(true)
                .categoryIds(Arrays.asList(1L, 2L))
                .build();

        SaveUserRequestDTO result = userService.saveUser(request);

        assertNotNull(result);
        assertEquals("dddd@ccc.com", result.getEmail());
    }

    @Test
    @DisplayName("메이커 저장 테스트")
    public void testMakerSave() {
        SaveMakerRequestDTO request = SaveMakerRequestDTO.builder()
                .name("aasd")
                .phone("010-7777-6666")
                .business(12345)
                .email("bbbb@bbb.com")
                .password(passwordEncoder.encode("1234"))
                .zipcode(12345)
                .address("테스트주소")
                .detailAddress("상세주소")
                .build();

        SaveMakerRequestDTO result = makerService.saveMaker(request);

        assertNotNull(result);
        assertEquals("bbbb@bbb.com", result.getEmail());
    }
}