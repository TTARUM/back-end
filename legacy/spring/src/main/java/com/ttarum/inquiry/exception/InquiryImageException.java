package com.ttarum.inquiry.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatusCode;

public class InquiryImageException extends TtarumException {

    public InquiryImageException(final HttpStatusCode status, final String message) {
        super(status, message);
    }

    public InquiryImageException(final HttpStatusCode status, final String message, final Throwable cause) {
        super(status, message, cause);
    }
}
