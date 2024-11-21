package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.dto.response.MonthlyUserCountResponseDTO;
import com.crofle.livecrowdfunding.repository.UserRepository;
import com.crofle.livecrowdfunding.service.AdminDashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
public class AdminDashBoardServiceImpl implements AdminDashBoardService {
    private final UserRepository userRepository;

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

}