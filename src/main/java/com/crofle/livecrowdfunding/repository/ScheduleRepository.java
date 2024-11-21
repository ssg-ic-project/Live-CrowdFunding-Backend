package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
