package com.ttarum.order.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class OrderException extends TtarumException {

    private static final String ORDER_NOT_FOUND_MESSAGE = "주문을 찾을 수 없습니다.";
    private static final String ITEM_NOT_FOUND_MESSAGE = "주문하신 상품을 찾을 수 없습니다.";

    public static OrderException notFound() {
        return new OrderException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND_MESSAGE);
    }

    public static OrderException itemNotFound() {
        return new OrderException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND_MESSAGE);
    }

    public OrderException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
