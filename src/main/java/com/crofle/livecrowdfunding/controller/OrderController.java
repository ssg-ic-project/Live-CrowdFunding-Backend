package com.crofle.livecrowdfunding.controller;

import com.crofle.livecrowdfunding.dto.request.OrderRequestDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.OrderHistoryResponseDTO;
import com.crofle.livecrowdfunding.dto.response.OrderResponseDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ProjectDetailResponseDTO;
import com.crofle.livecrowdfunding.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrderForPayment(@RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO orderResponseDTO = orderService.createOrder(orderRequestDTO);
        return ResponseEntity.created(URI.create("/order/" + orderResponseDTO.getId())).body(orderResponseDTO);
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
