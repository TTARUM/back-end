package com.ttarum.review.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ReviewForbiddenException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "권한이 없습니다.";

    public ReviewForbiddenException() {
        super(HttpStatus.FORBIDDEN, DEFAULT_MESSAGE);
    }

    public ReviewForbiddenException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
