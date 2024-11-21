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
    public ResponseEntity<StatisticsResponseDTO>getStats(){

        return null;
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

        return ResponseEntity.ok(userStats);
    }

    @GetMapping("/dashboard/yearly-revenue")
    public ResponseEntity<RevenueGraphResponseDTO>getTotalRevenue(){
        RevenueGraphResponseDTO revenueStats = RevenueGraphResponseDTO.builder()
                .labels(dashBoardService.getLast12Months())
                .revenue(dashBoardService.getNewTotalStats(oneYearAgo, now))
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

    @GetMapping("/dashboard/GraphE")
    public ResponseEntity<CategoryRevenueResponseDTO>getCategoryRevenue(){
        return null;
    }
}