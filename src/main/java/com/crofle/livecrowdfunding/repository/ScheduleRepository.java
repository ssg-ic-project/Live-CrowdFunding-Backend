package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Schedule;
import com.crofle.livecrowdfunding.dto.response.ScheduleChartResponseDTO;
import com.crofle.livecrowdfunding.dto.response.YesterdayStreamingResponseDTO;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s.date " +
            "FROM Schedule s " +
            "WHERE s.date >= :startDate AND s.date <= :endDate")
    List<LocalDateTime> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT new com.crofle.livecrowdfunding.dto.response.YesterdayStreamingResponseDTO(
                p.productName,
                s.totalViewer,
                COUNT(DISTINCT ph.orderId)
            )
            FROM Schedule s
            LEFT JOIN s.project p
            LEFT JOIN p.orders o
            LEFT JOIN PaymentHistory ph ON o.id = ph.orderId
            WHERE DATE(s.date) = DATE(:yesterday)
            GROUP BY p.productName, s.totalViewer
            ORDER BY COUNT(DISTINCT ph.orderId) DESC, s.totalViewer DESC
            
            """)
    List<YesterdayStreamingResponseDTO> findYesterdaySStats(@Param("yesterday") LocalDateTime yesterday);

    @Query(
            "SELECT p.id, s.date, p.productName, m.name, p.percentage, p.price, i.url FROM Schedule s " +
            "JOIN s.project p LEFT JOIN p.images i LEFT JOIN p.maker m " +
            "WHERE s.date >= :startDate AND s.date <= :endDate AND i.imageNumber = 1 " +
            "AND s.project.id = p.id")
    List<Object[]> findScheduleChart(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.date = :date")
    int countByDateWithLock(LocalDateTime date);
}
