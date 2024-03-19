package com.ttarum.order.domain;

public enum OrderStatus {
    PAYMENT("결제 완료"), SHIPPING("배송중"), COMPLETE("배송 완료");

    private final String message;

    OrderStatus(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
