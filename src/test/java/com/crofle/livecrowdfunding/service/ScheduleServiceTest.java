package com.crofle.livecrowdfunding.service;

import com.crofle.livecrowdfunding.dto.request.ScheduleRegisterRequestDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleChartResponseDTO;
import com.crofle.livecrowdfunding.dto.response.ScheduleReserveResponseDTO;
import com.crofle.livecrowdfunding.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Log4j2
@RequiredArgsConstructor
public class ScheduleServiceTest {
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    public void createScheduleTest() {
        Long projectId = 1L;
        LocalDateTime date = LocalDateTime.now();

        scheduleService.createSchedule(ScheduleRegisterRequestDTO.builder()
                .projectId(projectId)
                .date(date)
                .build());

        log.info("Schedule created");
    }

    @Test
    public void getReserveScheduleTest() {
        LocalDateTime startDate = LocalDateTime.now();

        List<ScheduleReserveResponseDTO> list = scheduleService.getReserveSchedule(startDate);

        log.info(list);
    }

    @Test
    public void getScheduleChartTest() {
        LocalDateTime startDate = LocalDateTime.now();

        List<ScheduleChartResponseDTO> list = scheduleService.getScheduleChart(startDate);

        log.info(list);
    }

    @Test
    public void updateScheduleStatusTest() {
        Long scheduleId = 5L;

        scheduleService.updateScheduleStatus(scheduleId);

        log.info("Schedule status updated");
    }





    // -------------------------------------------

    @Test

    @DisplayName("동시에 16개의 요청이 들어올 때 15개만 성공하고 1개는 실패해야 한다")
    void createScheduleConcurrentTest() throws InterruptedException {
        int numberOfThreads = 16;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 같은 시간대에 대한 예약 요청 DTO 생성
        LocalDateTime testDateTime = LocalDateTime.now().plusDays(1)
                .withHour(18).withMinute(0).withSecond(0).withNano(0);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // 각 스레드별로 새로운 트랜잭션에서 실행되도록 수정
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Long projectId = Long.valueOf(i + 5);

            Future<?> future = executorService.submit(() -> {
                try {
                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    transactionTemplate.execute(status -> {
                        try {
                            ScheduleRegisterRequestDTO requestDTO = new ScheduleRegisterRequestDTO(
                                    projectId,
                                    testDateTime
                            );
                            scheduleService.createSchedule(requestDTO);
                            successCount.incrementAndGet();
                        } catch (IllegalStateException e) {
                            failCount.incrementAndGet();
                        }
                        return null;
                    });
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await(10, TimeUnit.SECONDS);

        // 모든 Future가 완료될 때까지 대기
        for (Future<?> future : futures) {
            try {
                future.get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                // 로그 출력
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        // 검증
        assertAll(
                () -> assertEquals(15, successCount.get(), "성공한 예약 수가 15이어야 합니다"),
                () -> assertEquals(1, failCount.get(), "실패한 예약 수가 1이어야 합니다")
        );
    }

    @Test
    @Transactional
    @DisplayName("트랜잭션 롤백이 정상적으로 동작하는지 검증")
    void createScheduleRollbackTest() throws InterruptedException {
        int numberOfThreads = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        LocalDateTime testDateTime = LocalDateTime.now().plusDays(1)
                .withHour(15).withMinute(0).withSecond(0).withNano(0);

        List<Future<Boolean>> futures = new ArrayList<>();

        // 여러 스레드에서 동시에 예약 시도
        for (int i = 0; i < numberOfThreads; i++) {
            Long projectId = Long.valueOf(i + 1);

            Future<Boolean> future = executorService.submit(() -> {
                try {
                    ScheduleRegisterRequestDTO requestDTO = new ScheduleRegisterRequestDTO(
                            projectId,
                            testDateTime
                    );

                    scheduleService.createSchedule(requestDTO);
                    return true;
                } catch (IllegalStateException e) {
                    return false;
                } finally {
                    latch.countDown();
                }
            });

            futures.add(future);
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

//        // 실제 저장된 스케줄 수 확인
//        int savedScheduleCount = scheduleRepository.findByDateWithLock(testDateTime).size();
//        assertEquals(15, savedScheduleCount, "저장된 스케줄 수가 15개여야 합니다");
    }
}
