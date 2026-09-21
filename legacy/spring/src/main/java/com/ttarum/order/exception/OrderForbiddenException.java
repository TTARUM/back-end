package com.ttarum.order.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class OrderForbiddenException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "접근이 불가능합니다.";

    public OrderForbiddenException() {
        super(HttpStatus.FORBIDDEN, DEFAULT_MESSAGE);
    }

    public OrderForbiddenException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
