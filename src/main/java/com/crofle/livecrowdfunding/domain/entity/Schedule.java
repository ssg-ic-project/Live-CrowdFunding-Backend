package com.crofle.livecrowdfunding.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString(exclude = {"video", "script"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "date")
    private LocalDateTime date;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private Video video;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private Script script;
}