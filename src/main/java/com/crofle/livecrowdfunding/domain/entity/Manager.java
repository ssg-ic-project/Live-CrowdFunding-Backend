package com.crofle.livecrowdfunding.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = {"projects", "chatReports"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Manager {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(name = "identification_number", length = 20, nullable = false)
    private String identificationNumber;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(length = 50, nullable = false)
    private String password;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @OneToMany(mappedBy = "manager")
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "manager")
    @Builder.Default
    private List<ChatReport> chatReports = new ArrayList<>();
}
