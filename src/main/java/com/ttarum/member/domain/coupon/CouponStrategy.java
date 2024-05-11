package com.ttarum.member.domain.coupon;

public enum CouponStrategy {

    PERCENTAGE {
        @Override
        long calculate(final long originalPrice, final int discountPercent) {
            if (discountPercent == 100)
                return 0;
            return (int)(originalPrice * ((double) (100 - discountPercent) / 100));
        }
    }, ABSOLUTE {
        @Override
        long calculate(final long originalPrice, final int discountAmount) {
            if (originalPrice < discountAmount)
                return 0;
            return originalPrice - discountAmount;
        }
    };

    abstract long calculate(long existingPrice, int value);
}
