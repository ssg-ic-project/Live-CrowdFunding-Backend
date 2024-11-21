package com.crofle.livecrowdfunding.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Revenue {
    @Id
    @Column(name = "project_id")
    private Long projectId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @MapsId
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "total_amount", nullable = false)
    @Comment("프로젝트 성공 후 결산")
    private Integer totalAmount;
}