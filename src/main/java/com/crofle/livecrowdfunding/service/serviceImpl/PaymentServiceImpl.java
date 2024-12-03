package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.domain.entity.PaymentHistory;
import com.crofle.livecrowdfunding.dto.response.PaymentResponseDTO;
import com.crofle.livecrowdfunding.repository.PaymentHistoryRepository;
import com.crofle.livecrowdfunding.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {
    private final PaymentHistoryRepository paymentHistoryRepository;

    public void insertPaymentHistory(JSONObject jsonObject, String address) {
        try {
            String dateStr = (String) jsonObject.get("approvedAt");
            LocalDateTime localDateTime;

            try {
                localDateTime = OffsetDateTime
                        .parse(dateStr)
                        .toLocalDateTime();
            } catch (DateTimeParseException e) {
                log.error("날짜 파싱 실패: " + dateStr);
                throw new IllegalArgumentException("잘못된 날짜 형식입니다.", e);
            }
            //TossPay로 결제할 경우 '토스페이'
            PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.builder()
                    .paymentMethod("토스페이")
                    .orderId(Long.parseLong((String) jsonObject.get("orderId")))
                    //.orderId((String) jsonObject.get("orderId"))
                    .paymentAt(localDateTime)
                    .deliveryAddress(address)
                    .build();

            paymentHistoryRepository.insertPayment(paymentResponseDTO);

        } catch (Exception e) {
            log.error("결제 이력 저장 실패: ", e);
            throw new RuntimeException("결제 이력 저장 중 오류 발생", e);
        }
    }
}
