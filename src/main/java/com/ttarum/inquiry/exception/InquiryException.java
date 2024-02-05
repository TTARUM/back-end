package com.ttarum.inquiry.exception;

public class InquiryException extends RuntimeException {

    public InquiryException(final String message) {
        super(message);
    }

    public InquiryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
