package com.ttarum.member.repository;

import com.ttarum.member.domain.coupon.MemberCoupon;
import com.ttarum.member.dto.response.CouponResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    @Query("""
            SELECT new com.ttarum.member.dto.response.CouponResponse(c.id, c.name, c.couponStrategy, c.value)
            FROM MemberCoupon mc
            JOIN mc.coupon c
            WHERE mc.member.id = :memberId
        """)
    List<CouponResponse> findCouponListByMemberId(Long memberId);

    Optional<MemberCoupon> findByMemberIdAndCouponId(Long memberId, Long couponId);
    void deleteMemberCouponByMemberIdAndCouponId(Long memberId, Long couponId);
}
