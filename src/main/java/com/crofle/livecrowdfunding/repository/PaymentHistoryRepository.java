package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.ChatReport;
import com.crofle.livecrowdfunding.domain.entity.PaymentHistory;
import com.crofle.livecrowdfunding.dto.response.PaymentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    @Modifying
    @Transactional
    @Query(value="INSERT INTO payment_history(order_id, payment_method, delivery_address, payment_at)" +
        "VALUES (:#{#dto.orderId}, :#{#dto.paymentMethod}, :#{#dto.deliveryAddress}, :#{#dto.paymentAt})",
            nativeQuery = true)
    void insertPayment(
            @Param("dto") PaymentResponseDTO dto);

}
