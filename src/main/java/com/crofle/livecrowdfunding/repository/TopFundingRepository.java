package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.TopFunding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopFundingRepository extends JpaRepository<TopFunding, Long> {
}
