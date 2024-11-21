package com.crofle.livecrowdfunding.dto.request;

import com.crofle.livecrowdfunding.domain.entity.Project;
import com.crofle.livecrowdfunding.domain.entity.User;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Long userId;
    private Long projectId;
    private Integer amount;
}
