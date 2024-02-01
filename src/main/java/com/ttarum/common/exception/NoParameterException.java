package com.ttarum.common.exception;

public class NoParameterException extends RuntimeException {

    public NoParameterException(final String message) {
        super(message);
    }

    public NoParameterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
