package com.ttarum.review.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatusCode;

public class ReviewException extends TtarumException {

    public ReviewException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
