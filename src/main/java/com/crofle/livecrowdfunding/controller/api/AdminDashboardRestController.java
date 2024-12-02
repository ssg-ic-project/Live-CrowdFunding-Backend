package com.crofle.livecrowdfunding.controller.api;

import com.crofle.livecrowdfunding.dto.response.*;
import com.crofle.livecrowdfunding.service.AdminDashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminDashboardRestController {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime oneYearAgo = now.minusYears(1);

    //프론트에서 Fetchall 사용해 개별 엔드포인트 병렬 호출
    public final AdminDashBoardService dashBoardService;

    //refactoring시: 동시성 제어 사용시 axios사용??
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ProjectStatisticsResponseDTO>getStats(){
        log.info("got request");
        ProjectStatisticsResponseDTO dto = dashBoardService.getProjectStatistics();
        return ResponseEntity.ok(dto);
    }

    //월별(최근 12개월) 신규 가입자 수 (일반회원, 메이커, 총계)
    @GetMapping("/dashboard/new-users")
    public ResponseEntity<UserGraphResponseDTO>getNewUsers(){
        UserGraphResponseDTO userStats = UserGraphResponseDTO.builder()
                .labels(dashBoardService.getLast12Months())
                .users(dashBoardService.getNewUserStats(oneYearAgo, now))
                .makers(dashBoardService.getNewMakerStats(oneYearAgo, now))
                .total(dashBoardService.getNewTotalStats(oneYearAgo, now))
                .build();
        log.info("checking for makers");
        log.info(userStats.getMakers());
        return ResponseEntity.ok(userStats);
    }

    @GetMapping("/dashboard/yearly-revenue")
    public ResponseEntity<RevenueGraphResponseDTO>getTotalRevenue(){
        RevenueGraphResponseDTO revenueStats = RevenueGraphResponseDTO.builder()
                .labels(dashBoardService.getLast12Months())
                .revenue(dashBoardService.getRevenueStats(oneYearAgo))
                .build();

        return ResponseEntity.ok(revenueStats);
    }
    @GetMapping("/dashboard/GraphC")
    public ResponseEntity<PopularFundingResponseDTO>getPopularFunding(){
        return null;
    }

    //최근 12개월 이용자 수
    @GetMapping("/dashboard/current-users")
    public ResponseEntity<UserGraphResponseDTO>getCurrentUsers(){
        UserGraphResponseDTO userStats = UserGraphResponseDTO.builder()
                .labels(dashBoardService.getLast12Months())
                .users(dashBoardService.getCurrentUserStats(oneYearAgo, now))
                .makers(dashBoardService.getCurrentMakerStats(oneYearAgo, now))
                .total(dashBoardService.getCurrentTotalStats(oneYearAgo, now))
                .build();

        return ResponseEntity.ok(userStats);
    }

    //카테고리별 펀딩 수와 수익
    @GetMapping("/dashboard/revenue-by-category")
    public ResponseEntity<CategoryRevenueResponseDTO>getCategoryRevenue(){
        List<CategoryStatsResponseDTO> categoryStats = dashBoardService.getCategoryStats();
        log.info("checking category stats");
        log.info(categoryStats);

        // 카테고리 리스트 추출
        List<String> labels = categoryStats.stream()
                .map(CategoryStatsResponseDTO::getCategory)
                .collect(Collectors.toList());

        CategoryRevenueResponseDTO result = CategoryRevenueResponseDTO.builder()
                .labels(labels)
                .catrevenue(categoryStats)
                .build();

        return ResponseEntity.ok(result);
    }

    //스트리밍 프로젝트 별 구매자, 시청수
    @GetMapping("/dashboard/streaming-stats")
    public ResponseEntity<List<YesterdayStreamingResponseDTO>>getYesterdayStreamingStats(){
        return ResponseEntity.ok(dashBoardService.getYesterdaySStats());
    }
}