package com.crofle.livecrowdfunding.domain.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class EventLogId implements Serializable {
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "user_id")
    private Long userId;
}
