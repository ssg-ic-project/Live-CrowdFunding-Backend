package com.crofle.livecrowdfunding.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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

    @Column(name = "is_streaming", nullable = false)
    @Builder.Default
    private short isStreaming = 0;        // 0-> 방송 전(예약) 1-> 방송 중 2-> 방송 종료

    @Column(name = "total_viewer", nullable = false)
    @Builder.Default
    private Integer totalViewer = 0;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private Video video;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private Script script;

//    public void updateStreamingStatus() {
//        this.isStreaming = !this.isStreaming;
//    }
}