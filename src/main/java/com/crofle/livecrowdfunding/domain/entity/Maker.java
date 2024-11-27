package com.crofle.livecrowdfunding.domain.entity;

import com.crofle.livecrowdfunding.domain.enums.UserStatus;
import com.crofle.livecrowdfunding.dto.request.MakerInfoRequestDTO;
import com.crofle.livecrowdfunding.dto.request.UserInfoRequestDTO;
import com.crofle.livecrowdfunding.dto.response.MakerInfoResponseDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = {"projects"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Maker {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(nullable = false)
    private Long business;

    @Column(length = 255, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer zipcode;

    @Column(length = 30, nullable = false)
    private String address;

    @Column(name = "detail_address", length = 50, nullable = false)
    private String detailAddress;

    @Column(name = "unregistered_at")
    private LocalDateTime unregisteredAt;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Column(nullable = false)
    @Comment("활성화, 비활성화")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.활성화;

    @Builder.Default
    @OneToMany(mappedBy = "maker")
    private List<Project> projects = new ArrayList<>();

    public void updateMakerInfo(MakerInfoRequestDTO makerInfoRequestDTO) {
        this.phone = makerInfoRequestDTO.getPhone();
        this.address = makerInfoRequestDTO.getAddress();
        this.detailAddress = makerInfoRequestDTO.getDetailAddress();
    }
}
