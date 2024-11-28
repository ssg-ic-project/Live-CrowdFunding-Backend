package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.request.OrderRequestDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.OrderHistoryResponseDTO;
import com.crofle.livecrowdfunding.dto.response.OrderResponseDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Log4j2
public class OrderController {

    private final OrderService orderService;
    @PostMapping // 애초에 id만 리턴하면 되는데 우선은..
    public ResponseEntity<Long> createOrderForPayment(@RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO orderResponseDTO = orderService.createOrder(orderRequestDTO);
        return ResponseEntity.ok(orderResponseDTO.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderForPayment(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findOrder(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PageListResponseDTO<OrderHistoryResponseDTO>> getOrdersByUser(@PathVariable Long userId, @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(orderService.findByUser(userId, pageRequestDTO));
    }
}
