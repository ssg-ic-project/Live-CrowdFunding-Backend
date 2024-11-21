package com.crofle.livecrowdfunding.domain.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "payment_price", nullable = false)
    private Integer paymentPrice;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentHistory paymentHistory;
}
