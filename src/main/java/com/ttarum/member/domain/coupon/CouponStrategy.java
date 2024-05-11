package com.ttarum.member.domain.coupon;

public enum CouponStrategy {

    PERCENTAGE {
        @Override
        int calculate(final int originalPrice, final int discountPercent) {
            if (discountPercent == 100)
                return 0;
            return (int)(originalPrice * ((double) (100 - discountPercent) / 100));
        }
    }, ABSOLUTE {
        @Override
        int calculate(final int originalPrice, final int discountAmount) {
            if (originalPrice < discountAmount)
                return 0;
            return originalPrice - discountAmount;
        }
    };

    abstract int calculate(int existingPrice, int value);
}
