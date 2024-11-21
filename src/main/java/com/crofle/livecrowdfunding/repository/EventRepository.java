package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
