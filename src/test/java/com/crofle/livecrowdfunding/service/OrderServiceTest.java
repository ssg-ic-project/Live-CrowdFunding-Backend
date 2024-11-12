package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.entity.User;
import com.crofle.livecrowdfunding.dto.request.OrderRequestDTO;
import com.crofle.livecrowdfunding.dto.response.OrderResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Test
    public void testCreateOrder() {
        OrderRequestDTO orderRequestDTO = OrderRequestDTO.builder()
                .id(1L)
                .projectId(1L)
                .amount(50)
                .build();
        OrderResponseDTO orderResponseDTO = orderService.createOrder(orderRequestDTO);
        log.info(orderResponseDTO);
    }
}
