package com.crofle.livecrowdfunding.dto.response;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectListResponseDTO {
    private Long id;
    private String productName;
    private LocalDateTime startAt;
    private String status;
    private Integer totalPrice;
    private Integer percentage;
}
