package com.crofle.livecrowdfunding.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {
    @Id
    @Column(name = "schedule_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "media_url", nullable = false, length = 255)
    private String mediaUrl;

    // 미디어 URL 업데이트 메서드 추가
    public void updateMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}