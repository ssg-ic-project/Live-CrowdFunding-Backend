package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.Orders;
import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.entity.User;
import com.crofle.livecrowdfunding.dto.PageInfoDTO;
import com.crofle.livecrowdfunding.dto.request.OrderRequestDTO;
import com.crofle.livecrowdfunding.dto.request.PageRequestDTO;
import com.crofle.livecrowdfunding.dto.response.OrderHistoryResponseDTO;
import com.crofle.livecrowdfunding.dto.response.OrderResponseDTO;
import com.crofle.livecrowdfunding.dto.response.PageListResponseDTO;
import com.crofle.livecrowdfunding.dto.response.PageResponseDTO;
import com.crofle.livecrowdfunding.repository.OrdersRepository;
import com.crofle.livecrowdfunding.repository.ProjectRepository;
import com.crofle.livecrowdfunding.repository.UserRepository;
import com.crofle.livecrowdfunding.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final OrdersRepository ordersRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        User user = findUser(orderRequestDTO.getUserId());
        Project project = findProject(orderRequestDTO.getProjectId());
        int paymentPrice = calculatePaymentPrice(orderRequestDTO.getAmount(), project.getPrice());

        Orders order = Orders.builder().
                user(user).
                project(project).
                amount(orderRequestDTO.getAmount()).
                paymentPrice(paymentPrice).
                build();

        order = ordersRepository.save(order);
        OrderResponseDTO orderResponseDTO = modelMapper.map(order, OrderResponseDTO.class);
        return orderResponseDTO;
    }

    @Override
    @Transactional
    public OrderResponseDTO findOrder(Long id) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. ID: " + id));

        return modelMapper.map(order, OrderResponseDTO.class);
    }

    @Override
    public PageListResponseDTO<OrderHistoryResponseDTO> findByUser(Long userId, PageRequestDTO pageRequestDTO) {
        Page<OrderHistoryResponseDTO> orders = ordersRepository.findByUser(userId, pageRequestDTO.getPageable());
        return PageListResponseDTO.<OrderHistoryResponseDTO>builder()
                .dataList(orders.getContent())
                .pageInfoDTO(PageInfoDTO.withAll()
                        .pageRequestDTO(pageRequestDTO)
                        .total((int)orders.getTotalElements())
                        .build())
                .build();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트를 찾을 수 없습니다. ID: " + projectId));
    }

    private int calculatePaymentPrice(int amount, int projectPrice) {
        if (amount <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        return amount * projectPrice;
    }
}
