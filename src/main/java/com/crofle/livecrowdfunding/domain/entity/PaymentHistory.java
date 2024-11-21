package com.crofle.livecrowdfunding.domain.entity;

import com.crofle.livecrowdfunding.domain.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Order;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "PAYMENT_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentHistory {
    @Id
    private Long orderId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @MapsId  // orderId를 FK이자 PK로 사용
    @JoinColumn(name = "order_id")
    private Orders order;  // orders -> order로 변경 (단수형이 더 적절)

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "delivery_address", length = 30, nullable = false)
    private String deliveryAddress;

    @Column(name = "payment_at", nullable = false)
    private LocalDateTime paymentAt;

    @Builder.Default
    @Column(name = "refund_status", nullable = false)
    @Comment("0: No, 1: YES")
    private Integer refundStatus = 0;
}