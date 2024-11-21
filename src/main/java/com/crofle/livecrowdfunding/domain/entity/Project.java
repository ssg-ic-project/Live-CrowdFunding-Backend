package com.crofle.livecrowdfunding.domain.entity;

import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Setter
@ToString(exclude = {
        "maker",
        "manager",
        "ratePlan",
        "category",
        "schedules",
        "events",
        "chatReports",
        "likes",
        "orders",
        "images",
        "essentialDocuments",
        "revenue"
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id", nullable = false)
    private Maker maker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private RatePlan ratePlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "product_name", length = 20, nullable = false)
    private String productName;

    @Column(length = 50, nullable = false)
    private String summary;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer percentage = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false)
    private ProjectStatus reviewProjectStatus;

    @Lob
    @Column(name = "rejection_reason", columnDefinition = "LONGTEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "progress_status")
    private ProjectStatus progressProjectStatus;

    @Column(name = "goal_amount", nullable = false)
    private Integer goalAmount;

    @Column(name = "content_image", length = 200, nullable = false)
    private String contentImage;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChatReport> chatReports = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Liked> likes = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Orders> orders = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EssentialDocument> essentialDocuments = new ArrayList<>();

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private Revenue revenue;

    @PrePersist
    public void prePersist() {
        this.reviewProjectStatus = ProjectStatus.검토중;
    }
}
