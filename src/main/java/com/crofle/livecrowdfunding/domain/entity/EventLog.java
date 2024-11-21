package com.crofle.livecrowdfunding.domain.entity;

import com.crofle.livecrowdfunding.domain.id.EventLogId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "EVENT_LOG")
@Getter
@Builder
@ToString(exclude = {"event", "user"})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventLog {
    @EmbeddedId
    private EventLogId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "winning_prize", nullable = false, length = 255)
    private String winningPrize;

    @Column(name = "winning_date", nullable = false)
    private LocalDateTime winningDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}