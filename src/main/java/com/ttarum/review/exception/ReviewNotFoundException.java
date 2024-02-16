package com.ttarum.review.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ReviewNotFoundException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "해당 리뷰를 찾을 수 없습니다.";

    public ReviewNotFoundException() {
        this(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE);
    }

    public ReviewNotFoundException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
