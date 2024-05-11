package com.ttarum.member.domain.coupon;

public enum CouponStrategy {

    PERCENTAGE {
        @Override
        double calculate(final int originalPrice, final int newPrice) {
            if (newPrice == 100)
                return 0;
            return originalPrice * ((double) (100 - newPrice) / 100);
        }
    }, ABSOLUTE {
        @Override
        double calculate(final int originalPrice, final int newPrice) {
            if (originalPrice < newPrice)
                return 0;
            return originalPrice - (double) newPrice;
        }
    };

    abstract double calculate(int existingPrice, int applicableValue);
}
