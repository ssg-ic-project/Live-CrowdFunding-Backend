package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.MakerInfoRequestDTO;
import com.crofle.livecrowdfunding.dto.response.MakerInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor
@Log4j2
public class MakerServiceTest {
    @Autowired
    private MakerService makerService;

    @Test
    public void findMakerTest() {
        Long makerId = 1L;
        MakerInfoResponseDTO makerInfoResponseDTO = makerService.findMaker(makerId);

        log.info(makerInfoResponseDTO);
    }

    @Test
    public void updateMakerTest() {
        Long makerId = 1L;
        MakerInfoRequestDTO makerInfoRequestDTO = new MakerInfoRequestDTO();

        makerInfoRequestDTO.setPhone("010-1234-5678");
        makerInfoRequestDTO.setAddress("서울시 강남구");
        makerInfoRequestDTO.setDetailAddress("테헤란로 427");

        makerService.updateMaker(makerId, makerInfoRequestDTO);
    }
}
