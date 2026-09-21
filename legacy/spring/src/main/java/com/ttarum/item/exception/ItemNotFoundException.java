package com.ttarum.item.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ItemNotFoundException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "해당 제품을 찾을 수 없습니다.";

    public ItemNotFoundException() {
        this(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE);
    }

    public ItemNotFoundException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
