package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {

    //Revenue 12개월
    @Query(value = """
    SELECT d.month, COALESCE(SUM(p.price * (p.percentage / 100) * (pl.charge / 100)), 0) as revenue
    FROM (SELECT DATE_FORMAT(:startDate + INTERVAL n MONTH, '%Y-%m') as month
        FROM (SELECT 0 as n UNION ALL SELECT 1 UNION ALL SELECT 2 
            UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
            UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8
            UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11) numbers
    ) d
    LEFT JOIN project p ON DATE_FORMAT(p.end_at, '%Y-%m') = d.month AND p.progress_status = '성공'
    LEFT JOIN rate_plan pl ON p.plan_id = pl.id GROUP BY d.month ORDER BY d.month """, nativeQuery = true)
    List<Object[]> calculateMonthlyRevenue(@Param("startDate") LocalDateTime startDate);

    //카테고리별 월간 성공 프로젝트 수 & 수익
    @Query(value = """
    SELECT 
        c.classification as category_name,
        COUNT(p.id) as success_count,
        COALESCE(ROUND(SUM(p.price * p.percentage / 100 * pl.charge / 100)), 0) as revenue
    FROM category c
    LEFT JOIN project p ON c.id = p.category_id 
        AND p.progress_status = '성공'
        AND DATE_FORMAT(p.end_at, '%Y-%m') = DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 MONTH), '%Y-%m')
    LEFT JOIN rate_plan pl ON p.plan_id = pl.id
    GROUP BY c.classification
    ORDER BY c.classification
    """, nativeQuery = true)
    List<Object[]> calculateLastMonthCategoryStats();
}
