package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {
}
