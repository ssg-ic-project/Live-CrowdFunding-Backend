package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
}
