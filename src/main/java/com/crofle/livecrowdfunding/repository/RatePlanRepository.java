package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

}
