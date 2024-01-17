package com.ttarum.review.exception;

public class ReviewException extends RuntimeException {

    public ReviewException(final String message) {
        super(message);
    }

    public ReviewException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
