package com.ttarum.order.domain;

import com.ttarum.common.domain.BaseEntity;
import com.ttarum.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "`order`")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int")
    private Long id;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "comment", nullable = false, length = 100)
    private String comment;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "delivery_fee", nullable = false, columnDefinition = "int")
    private Integer deliveryFee;

    @Column(name = "recipient", nullable = false, length = 20)
    private String recipient;

    @Column(name = "price", nullable = false, columnDefinition = "int")
    private Long price;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, columnDefinition = "int")
    private Member member;

}