package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.entity.Schedule;
import com.crofle.livecrowdfunding.dto.request.ScheduleRegisterRequestDTO;
import com.crofle.livecrowdfunding.dto.response.LiveFundingInMainResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleChartResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleReserveResponseDTO;
import com.crofle.livecrowdfunding.dto.response.TimeSlotResponseDTO;
import com.crofle.livecrowdfunding.repository.ProjectRepository;
import com.crofle.livecrowdfunding.repository.ScheduleRepository;
import com.crofle.livecrowdfunding.service.ProjectService;
import com.crofle.livecrowdfunding.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cglib.core.Local;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @Override
    public void createSchedule(ScheduleRegisterRequestDTO requestDTO) {
        // 락을 걸고 해당 시간대의 예약 수를 확인
        int count = scheduleRepository.countByDateWithLock(requestDTO.getDate());

        if (count >= 3) {
            throw new IllegalStateException("해당 시간대는 이미 예약이 마감되었습니다.");
        }

        Schedule schedule = Schedule.builder()
                .project(projectRepository.findById(requestDTO.getProjectId()).orElseThrow())
                .date(requestDTO.getDate())
                .build();
        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ScheduleReserveResponseDTO> getReserveSchedule(LocalDateTime nowDate) {

        LocalDateTime endDate = nowDate.plusDays(7);
        LocalDateTime startDate = nowDate.plusDays(1).withHour(0).withMinute(0).withSecond(0);

        List<LocalDateTime> existingSchedules = scheduleRepository.findByDateBetween(startDate, endDate);

        List<ScheduleReserveResponseDTO> availableSlots = new ArrayList<>();
        LocalDateTime current = startDate;

        // 날짜별로 12시부터 23시까지의 슬롯을 생성
        while (!current.isAfter(endDate)) {
            List<TimeSlotResponseDTO> dailySlots = new ArrayList<>();
            LocalDateTime currentDate = current;  // Capture for lambda

            // 12시부터 23시까지 순회
            for (int hour = 12; hour <= 23; hour++) {
                LocalDateTime timeSlot = current.withHour(hour).withMinute(0).withSecond(0);
                final int currentHour = hour;

                // 해당 시간대의 예약된 방송 수 계산 - 날짜와 시간이 모두 일치하는 경우를 체크
                long bookedCount = existingSchedules.stream()
                        .filter(schedule ->
                                schedule.toLocalDate().equals(currentDate.toLocalDate()) &&
                                        schedule.getHour() == currentHour
                        )
                        .count();

                // 일단 3개 예약 받음
                int availableCount = 3 - (int) bookedCount;

                dailySlots.add(new TimeSlotResponseDTO(
                        timeSlot,
                        availableCount > 0,
                        availableCount
                ));
            }

            availableSlots.add(new ScheduleReserveResponseDTO(
                    current.toLocalDate(),
                    dailySlots
            ));

            current = current.plusDays(1);
        }

        return availableSlots;
    }

    @Override
    public List<ScheduleChartResponseDTO> getScheduleChart(LocalDateTime nowDate) {

        LocalDateTime endDate = nowDate.plusDays(7);
        LocalDateTime startDate = nowDate.withHour(0).withMinute(0).withSecond(0);

        List<Object[]> schedules = scheduleRepository.findScheduleChart(startDate, endDate);

        return schedules.stream()
                .map(schedule -> {
                    double percentage = (int) schedule[4];
                    double originalPrice = (int) schedule[5];
                    double discountRate = percentage / 100.0;
                    int discountedPrice = (int) (originalPrice * (1 - discountRate));

                    return ScheduleChartResponseDTO.builder()
                            .projectId((Long) schedule[0])
                            .date(((LocalDateTime) schedule[1]).format(DateTimeFormatter.ofPattern("MM.dd")))
                            .time(((LocalDateTime) schedule[1]).format(DateTimeFormatter.ofPattern("HH:mm")))
                            .productName((String) schedule[2])
                            .makerName((String) schedule[3])
                            .percentage((int) schedule[4])
                            .originalPrice((int) schedule[5])
                            .discountedPrice(discountedPrice)
                            .thumbnailUrl((String) schedule[6])
                            .build();
                })
                .toList();
    }

    @Transactional
    @Override
    public void updateScheduleStatus(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        switch (schedule.getIsStreaming()) {
            case 0: //방송 전 -> 방송중
                schedule.setIsStreaming((short) 1);
                sendLiveProductUpdate(schedule);
                break;
            case 1: //방송중 -> 방송 종료
                schedule.setIsStreaming((short) 2);
                sendLiveProductUpdate(schedule);
                break;
            default:
                throw new IllegalArgumentException("Invalid status");
        }
    }

    private void sendLiveProductUpdate(Schedule schedule) {
        Project project = schedule.getProject();

        // 클라이언트에게 전송할 데이터 구성
        LiveFundingInMainResponseDTO dto = LiveFundingInMainResponseDTO.builder()
                .scheduleId(schedule.getId())
                .url(project.getImages().get(0).getUrl())
                .productName(project.getProductName())
                .percentage(project.getPercentage())
                .classification(project.getCategory().getClassification())
                .remainingTime(ChronoUnit.DAYS.between(project.getStartAt(), project.getEndAt()))
                .isStreaming(schedule.getIsStreaming())
                .build();

        // WebSocket을 통해 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/sub/live-products", dto);
    }
}
