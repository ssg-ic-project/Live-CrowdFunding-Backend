package com.crofle.livecrowdfunding.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "CHAT_REPORT")
@Builder
@ToString(exclude = {"user", "project", "manager"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    @Column(length = 255)
    private String reason;

    @Column(name = "chat_message", nullable = false, length = 255)
    private String chatMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
