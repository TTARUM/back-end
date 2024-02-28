package com.ttarum.order.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class OrderNotFoundException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "주문을 찾을 수 없습니다.";
    private static final HttpStatusCode DEFAULT_STATUS = HttpStatus.NOT_FOUND;

    public OrderNotFoundException() {
        super(DEFAULT_STATUS, DEFAULT_MESSAGE);
    }

    public OrderNotFoundException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
