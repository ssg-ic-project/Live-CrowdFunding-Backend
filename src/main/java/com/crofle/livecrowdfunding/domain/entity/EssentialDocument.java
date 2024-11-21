package com.crofle.livecrowdfunding.domain.entity;

import com.crofle.livecrowdfunding.domain.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "ESSENTIAL_DOCUMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EssentialDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 200)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false)
    private DocumentType docType;

    @Column(nullable = false)
    private String name;
}
