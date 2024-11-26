package com.crofle.livecrowdfunding.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.jcip.annotations.Immutable;

@Getter
@Setter
@Entity
@Table(name="admin_account_view")
@Immutable
public class AdminAccountView {
    @Id
    private Long id;
    @Column(name="identification_number")
    private String idNum;
    private String email;
    private String password;
}