package com.ttarum.review.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class DuplicatedReviewException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "리뷰가 이미 존재합니다.";

    public DuplicatedReviewException() {
        super(HttpStatus.BAD_REQUEST, DEFAULT_MESSAGE);
    }

    public DuplicatedReviewException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
