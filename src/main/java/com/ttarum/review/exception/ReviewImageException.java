package com.ttarum.review.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatusCode;

public class ReviewImageException extends TtarumException {

    public ReviewImageException(final HttpStatusCode status, final String message) {
        super(status, message);
    }

    public ReviewImageException(final HttpStatusCode status, final String message, final Throwable throwable) {
        super(status, message, throwable);
    }
}
