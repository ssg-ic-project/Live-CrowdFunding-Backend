package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Orders;
import com.crofle.livecrowdfunding.dto.response.OrderHistoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    @Query("SELECT new com.crofle.livecrowdfunding.dto.response.OrderHistoryResponseDTO(" +
            "ph.paymentAt, " +
            "i.url, " +
            "p.productName, " +
            "o.amount, " +
            "o.paymentPrice) " +
            "FROM Orders o " +
            "JOIN o.project p " +
            "JOIN o.paymentHistory ph " +
            "JOIN p.images i " +
            "WHERE o.user.id = :userId " +
            "AND i.imageNumber = (SELECT MIN(img.imageNumber) FROM Image img WHERE img.project = p) " +
            "ORDER BY ph.paymentAt DESC")
    Page<OrderHistoryResponseDTO> findByUser(Long userId, Pageable pageable);
}
