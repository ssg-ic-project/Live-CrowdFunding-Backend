package com.crofle.livecrowdfunding.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
public class EssentialDocument {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name="project_id")
    private Project projectId;
    private String document;
}
