package com.ttarum.member.domain.coupon;

public enum CouponStrategy {

    PERCENTAGE {
        @Override
        long calculateDiscount(final long originalPrice, final int discountPercent) {
            if (discountPercent == 100)
                return originalPrice;
            return (int)(originalPrice * ((double) discountPercent / 100));
        }
    }, ABSOLUTE {
        @Override
        long calculateDiscount(final long originalPrice, final int discountAmount) {
            if (originalPrice < discountAmount)
                return originalPrice;
            return discountAmount;
        }
    };

    abstract long calculateDiscount(long existingPrice, int value);
}
