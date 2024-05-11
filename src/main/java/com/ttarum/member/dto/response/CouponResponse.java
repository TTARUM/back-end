package com.ttarum.member.dto.response;

import com.ttarum.member.domain.coupon.CouponStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "쿠폰 조회 DTO")
public class CouponResponse {
    @Schema(description = "쿠폰 이름", example = "신규 가입 쿠폰")
    private final String name;

    @Schema(description = "쿠폰 할인 방식", example = "PERCENTAGE")
    private final CouponStrategy couponStrategy;
}
