package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.EventLog;
import com.crofle.livecrowdfunding.dto.projection.EventLogWithEventNameDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    @Query("SELECT new com.crofle.livecrowdfunding.dto.projection.EventLogWithEventNameDTO(el.winningDate, el.winningPrize, e.name) FROM EventLog el JOIN el.event e WHERE el.user.id = :userId ORDER BY el.createdAt DESC")
    Page<EventLogWithEventNameDTO> findByUser(Long userId, Pageable pageable);
}
