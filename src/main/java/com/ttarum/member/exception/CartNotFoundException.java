package com.ttarum.member.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class CartNotFoundException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "해당 제품이 장바구니에 존재하지 않습니다.";
    private static final HttpStatusCode DEFAULT_STATUS = HttpStatus.NOT_FOUND;

    public CartNotFoundException() {
        super(DEFAULT_STATUS, DEFAULT_MESSAGE);
    }

    public CartNotFoundException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
