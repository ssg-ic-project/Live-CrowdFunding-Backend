package com.crofle.livecrowdfunding.domain.entity;

import com.crofle.livecrowdfunding.domain.enums.UserStatus;
import com.crofle.livecrowdfunding.dto.request.UserInfoRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
@Getter
@Builder
@ToString(exclude = {"interests", "likes", "orders", "chatReports"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 30)
    private String nickname;

    @Column(length = 30, nullable = false)
    private String phone;

    @Column(nullable = false)
    private Boolean gender;

    @Column(length = 10, nullable = false)
    private String birth;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer zipcode;

    @Column(length = 50, nullable = false)
    private String address;

    @Column(name = "detail_address", length = 50, nullable = false)
    private String detailAddress;

    @Builder.Default
    @Column(name = "login_method", nullable = false)
    private Boolean loginMethod = false;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Column(name = "unregistered_at")
    private LocalDateTime unregisteredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Builder.Default
    @Column(nullable = false)
    private Boolean notification = false;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserInterest> interests = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Liked> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Orders> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ChatReport> chatReports = new ArrayList<>();

    public void updateUserInfo(UserInfoRequestDTO userInfoRequestDTO) {
        this.nickname = userInfoRequestDTO.getNickname();
        this.phone = userInfoRequestDTO.getPhone();
        this.address = userInfoRequestDTO.getAddress();
        this.detailAddress = userInfoRequestDTO.getDetailAddress();
        this.notification = userInfoRequestDTO.getNotification();
    }

    public void setInterests(List<UserInterest> interests) {
        this.interests.clear();
        if (interests != null) {
            this.interests.addAll(interests);
        }
    }

    // 상태 변경을 위한 비즈니스 메서드들
    public void activateUser() {
        this.status = UserStatus.활성화;
    }

    public void deactivateUser() {
        this.status = UserStatus.비활성화;
    }

    public void suspendUser() {
        this.status = UserStatus.정지;
    }

    // 또는 하나의 메서드로
    public void updateStatus(UserStatus newStatus) {
        this.status = newStatus;
    }
}
