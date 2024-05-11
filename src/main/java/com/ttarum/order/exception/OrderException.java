package com.ttarum.order.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class OrderException extends TtarumException {

    private static final String ORDER_NOT_FOUND_MESSAGE = "주문을 찾을 수 없습니다.";
    private static final String ITEM_NOT_FOUND_MESSAGE = "주문하신 상품을 찾을 수 없습니다.";
    private static final String COUPON_NOT_FOUND = "쿠폰을 찾을 수 없습니다.";
    private static final String PRICE_NOT_MATCH_MESSAGE = "주문 금액이 일치하지 않습니다.";

    public static OrderException notFound() {
        return new OrderException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND_MESSAGE);
    }

    public static OrderException itemNotFound() {
        return new OrderException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND_MESSAGE);
    }

    public static OrderException couponNotFound() {
        return new OrderException(HttpStatus.NOT_FOUND, COUPON_NOT_FOUND);
    }

    public static OrderException priceNotMatch() {
        return new OrderException(HttpStatus.BAD_REQUEST, PRICE_NOT_MATCH_MESSAGE);
    }

    public OrderException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
