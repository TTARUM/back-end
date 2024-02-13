package com.ttarum.inquiry.exception;

public class ImageException extends RuntimeException {

    public ImageException(final String message) {
        super(message);
    }

    public ImageException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
