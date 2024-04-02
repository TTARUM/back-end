package com.ttarum.order.domain;

public enum PaymentMethod {
    CREDIT_CARD("신용카드");

    private final String message;

    PaymentMethod(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
