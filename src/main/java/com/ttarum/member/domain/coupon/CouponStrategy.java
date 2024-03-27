package com.ttarum.member.domain.coupon;

public enum CouponStrategy {

    PERCENTAGE {
        @Override
        double calculate(final int existingPrice, final int applicableValue) {
            if (applicableValue == 100)
                return 0;
            return existingPrice * ((double) (100 - applicableValue) / 100);
        }
    }, ABSOLUTE {
        @Override
        double calculate(final int existingPrice, final int applicableValue) {
            if (existingPrice < applicableValue)
                return 0;
            return existingPrice - (double) applicableValue;
        }
    };

    abstract double calculate(int existingPrice, int applicableValue);
}
