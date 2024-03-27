package com.ttarum.member.domain.coupon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private CouponStrategy couponStrategy;

    private int value;

    public double calculate(int existingPrice) {
        return couponStrategy.calculate(existingPrice, value);
    }
}
