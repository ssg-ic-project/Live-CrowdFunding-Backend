package com.crofle.livecrowdfunding.domain.entity;

import com.crofle.livecrowdfunding.domain.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@Getter
@Setter
@ToString
@Entity
@Table(name = "account_view")
@Immutable
public class AccountView {

    @Id
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
