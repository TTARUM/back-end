package com.ttarum.member.repository;

import com.ttarum.member.domain.coupon.MemberCoupon;
import com.ttarum.member.dto.response.CouponResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    @Query("""
            SELECT new com.ttarum.member.dto.response.CouponResponse(c.name, c.couponStrategy, c.value)
            FROM MemberCoupon mc
            JOIN mc.coupon c
            WHERE mc.member.id = :memberId
        """)
    List<CouponResponse> findCouponListByMemberId(Long memberId);
}
