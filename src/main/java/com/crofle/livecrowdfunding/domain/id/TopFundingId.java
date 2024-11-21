package com.crofle.livecrowdfunding.domain.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopFundingId implements Serializable {

    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "project_id", columnDefinition = "BIGINT UNSIGNED")
    private Long projectId;
}
