package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.PaymentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

}
