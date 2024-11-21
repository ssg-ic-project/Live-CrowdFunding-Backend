package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.ChatReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatReportRepository extends JpaRepository<ChatReport, Long> { //N+1 문제 방지용
    @Query("SELECT cr FROM ChatReport cr " +
            "LEFT JOIN FETCH cr.project p " +
            "LEFT JOIN FETCH cr.user u " +
            "LEFT JOIN FETCH p.manager m")
    Page<ChatReport> findAll(Pageable pageable);

}