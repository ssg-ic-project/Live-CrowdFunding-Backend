package com.crofle.livecrowdfunding.repository;

import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import com.crofle.livecrowdfunding.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
  
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.orders o " +
            "LEFT JOIN FETCH o.paymentHistory " +
            "LEFT JOIN FETCH p.category " +
            "WHERE p.id = :id")
    Optional<Project> findByIdWithOrders(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.id = :id")
    Optional<Project> findByIdWithImages(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.essentialDocuments " +
            "WHERE p.id = :id")
    Optional<Project> findByIdWithDocuments(@Param("id") Long id);

    @Query("SELECT p FROM Project p WHERE p.reviewProjectStatus IN :statuses")
    Page<Project> findByReviewStatuses(@Param("statuses") List<ProjectStatus> statuses, Pageable pageable);

    // 펀딩 진행 중인 프로젝트 조회 (승인 && 펀딩중)
    @Query("SELECT p FROM Project p WHERE p.reviewProjectStatus = :reviewStatus AND p.progressProjectStatus = :progressStatus")
    Page<Project> findByReviewStatusAndProgressStatus(
            @Param("reviewStatus") ProjectStatus reviewStatus,
            @Param("progressStatus") ProjectStatus progressStatus,
            Pageable pageable
    );

    // 펀딩 종료된 프로젝트 조회 (성공, 미달성)
    @Query("SELECT p FROM Project p WHERE p.progressProjectStatus IN :statuses")
    Page<Project> findByProgressStatuses(@Param("statuses") List<ProjectStatus> statuses, Pageable pageable);

    // 메인 화면에서 라이브중인 프로젝트 조회
    @Query("SELECT new com.crofle.livecrowdfunding.dto.response.LiveFundingInMainResponseDTO(p.id, i.url, p.productName, p.percentage, p.category.classification, CAST(DATEDIFF(p.endAt, CURRENT_DATE) AS long)) FROM Project p " +
            "JOIN p.schedules s LEFT JOIN p.images i " +
            "WHERE s.isStreaming = true AND i.imageNumber = 1 " +
            "ORDER BY s.totalViewer DESC")
    List<LiveFundingInMainResponseDTO> findLiveFundingInMain();

    @Query("SELECT new com.crofle.livecrowdfunding.dto.response.TopFundingInMainResponseDTO(p.id, t.ranking, i.url, p.productName, p.percentage, p.category.classification, CAST(DATEDIFF(p.endAt, CURRENT_DATE) AS long)) FROM Project p " +
            "JOIN p.topFundings t LEFT JOIN p.images i " +
            "WHERE i.imageNumber = 1 AND t.updatedAt = :today " +
            "ORDER BY t.ranking ASC")
    List<TopFundingInMainResponseDTO> findTopFundingInMain(@Param("today") LocalDateTime today);

    @Query("SELECT new com.crofle.livecrowdfunding.dto.response.ProjectLiveVODResponseDTO(p.id, i.url, p.productName, p.percentage, p.category.classification, CAST(DATEDIFF(p.endAt, CURRENT_DATE) AS long), s.isStreaming) FROM Project p " +
            "JOIN p.schedules s LEFT JOIN p.images i LEFT JOIN p.maker m LEFT JOIN s.video v " +
            "WHERE i.imageNumber = 1 and p.progressProjectStatus = '펀딩중' AND (s.isStreaming = true OR v.mediaUrl IS NOT NULL )" +
            "ORDER BY s.date DESC")
    List<ProjectLiveVODResponseDTO> findLiveAndVODProjectList();

    @Query("SELECT new com.crofle.livecrowdfunding.dto.response.ProjectWithConditionResponseDTO(p.id, i.url, p.productName, p.percentage, p.category.classification, CAST(DATEDIFF(p.endAt, CURRENT_DATE) AS long), s.isStreaming) FROM Project p " +
            "LEFT JOIN p.images i WITH i.imageNumber = 1 " +
            "LEFT JOIN p.schedules s " +
            "WHERE p.progressProjectStatus = '펀딩중' " +
            "AND p.productName LIKE %:keyword% " +
            "ORDER BY p.id DESC")
    Page<ProjectWithConditionResponseDTO> findByKeywordProject(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new com.crofle.livecrowdfunding.dto.response.ProjectWithConditionResponseDTO(p.id, i.url, p.productName, p.percentage, p.category.classification, CAST(DATEDIFF(p.endAt, CURRENT_DATE) AS long), s.isStreaming) FROM Project p " +
            "LEFT JOIN p.images i WITH i.imageNumber = 1 " +
            "LEFT JOIN p.schedules s " +
            "WHERE p.progressProjectStatus = '펀딩중' " +
            "AND p.category.id = :categoryId " +
            "ORDER BY p.id DESC")
    Page<ProjectWithConditionResponseDTO> findByCategoryIdProject(@Param("categoryId") Long categoryId, Pageable pageable);

    //관리자 대시보드용
    @Query("""
            SELECT new com.crofle.livecrowdfunding.dto.response.ProjectStatisticsResponseDTO(
                CAST((SELECT COUNT(p1.id) 
                 FROM Project p1 
                 WHERE DATE(p1.startAt) = CURRENT_DATE) AS Long),
                 
                CAST((SELECT COUNT(p2.id) 
                 FROM Project p2 
                 WHERE YEAR(p2.startAt) = YEAR(CURRENT_DATE) 
                 AND MONTH(p2.startAt) = MONTH(CURRENT_DATE)) AS Long),
                 
                CAST((SELECT COUNT(p3.id) 
                 FROM Project p3 
                 WHERE YEAR(p3.startAt) = YEAR(CURRENT_DATE)) AS Long),
                 
                CAST((SELECT COUNT(p4.id) 
                 FROM Project p4 
                 WHERE p4.reviewProjectStatus = com.crofle.livecrowdfunding.domain.enums.ProjectStatus.검토중
                 AND DATE(p4.startAt) = CURRENT_DATE) AS Long),
                 
                CAST((SELECT COUNT(p5.id) 
                 FROM Project p5 
                 WHERE p5.progressProjectStatus = com.crofle.livecrowdfunding.domain.enums.ProjectStatus.펀딩중)AS Long)
            )
            """)
    ProjectStatisticsResponseDTO getProjectStatistics();

}
