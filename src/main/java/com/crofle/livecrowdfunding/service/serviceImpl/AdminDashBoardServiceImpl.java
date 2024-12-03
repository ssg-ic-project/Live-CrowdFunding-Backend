package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.dto.response.*;
import com.crofle.livecrowdfunding.repository.ProjectRepository;
import com.crofle.livecrowdfunding.repository.RevenueRepository;
import com.crofle.livecrowdfunding.repository.ScheduleRepository;
import com.crofle.livecrowdfunding.repository.UserRepository;
import com.crofle.livecrowdfunding.service.AdminDashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
public class AdminDashBoardServiceImpl implements AdminDashBoardService {
    private final UserRepository userRepository;
    private final RevenueRepository revenueRepository;
    private final ScheduleRepository scheduleRepository;
    private final ProjectRepository projectRepository;

    //최근 12개월 list
    @Override
    public List<String> getLast12Months(){
        return Stream.iterate(LocalDate.now().minusMonths(11),
                date->date.plusMonths(1))
                .limit(12)
                .map(date->date.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .collect(Collectors.toList());
    }
    //가입 현황 그래프
    @Override
    public List<MonthlyUserCountResponseDTO>getNewUserStats(LocalDateTime start, LocalDateTime end){
        List<Object[]> result = userRepository.countMonthlyNewUsers(start, end);
        return result.stream()
                .map(row->new MonthlyUserCountResponseDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }
    @Override
    public List<MonthlyUserCountResponseDTO> getNewMakerStats(LocalDateTime start, LocalDateTime end) {
        List<Object[]> result = userRepository.countMonthlyNewMakers(start, end);
        return result.stream()
                .map(row->new MonthlyUserCountResponseDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<MonthlyUserCountResponseDTO> getNewTotalStats(LocalDateTime start, LocalDateTime end) {
        List<Object[]> result = userRepository.countMonthlyNewTotal(start, end);

        return result.stream()
                .map(row->new MonthlyUserCountResponseDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    //이용자 현황 그래프
    @Override
    public List<MonthlyUserCountResponseDTO> getCurrentUserStats(LocalDateTime start, LocalDateTime end) {
        List<Object[]> result = userRepository.countMonthlyCurrentUsers(start, end);
        return result.stream()
                .map(row->new MonthlyUserCountResponseDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<MonthlyUserCountResponseDTO> getCurrentMakerStats(LocalDateTime start, LocalDateTime end) {
        List<Object[]> result = userRepository.countMonthlyCurrentMakers(start, end);
        return result.stream()
                .map(row->new MonthlyUserCountResponseDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    //Total
    @Override
    public List<MonthlyUserCountResponseDTO> getCurrentTotalStats(LocalDateTime start, LocalDateTime end) {
        List<Object[]> result = userRepository.countMonthlyCurrentTotal(start, end);
        return result.stream()
                .map(row -> new MonthlyUserCountResponseDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    //Revenue
    @Override
    public List<MonthlyRevenueResponseDTO> getRevenueStats(LocalDateTime start) {
        List<Object[]> result = revenueRepository.calculateMonthlyRevenue(start);
        return result.stream()
                .map(row -> new MonthlyRevenueResponseDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public FundingStatusResponseDTO getSuccessRateStatus(){
        List<Object[]> results = projectRepository.getLastMonthProjectStatus();
        if (results.isEmpty() || results.get(0) == null) {
            return FundingStatusResponseDTO.builder()
                    .success(0)
                    .failed(0)
                    .build();
        }
        Object[] counts = results.get(0);
        return FundingStatusResponseDTO.builder()
                .success(((Long) counts[0]).intValue())
                .failed(((Long) counts[1]).intValue())
                .build();
//        Object[] counts = projectRepository.getLastMonthProjectStatus();
//        return FundingStatusResponseDTO.builder()
//                .success(((Number)counts[0]).intValue())
//                .failed(((Number)counts[1]).intValue())
//                .build();
    }




    //카테고리별 수익
    @Override
    public List<CategoryStatsResponseDTO> getCategoryStats() {
        List<Object[]> result = revenueRepository.calculateLastMonthCategoryStats();
        return result.stream()
                .map(row -> new CategoryStatsResponseDTO(
                        (String) row[0], //category name
                        ((Number) row[1]).longValue(), //success_count
                        ((Number) row[2]).doubleValue() //revenue
                ))
                .collect(Collectors.toList());
    }
    //스트리밍 별 구매자, 스트리밍 수
    @Override
    public List<YesterdayStreamingResponseDTO>getYesterdaySStats(){
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return scheduleRepository.findYesterdaySStats(yesterday);
    }

    @Override
    public ProjectStatisticsResponseDTO getProjectStatistics(){
        return projectRepository.getProjectStatistics();
    }

}