package com.crofle.livecrowdfunding.dto.response;

import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
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
    private ProjectStatus status;
    private Long totalPrice;
    private Integer percentage;
}
