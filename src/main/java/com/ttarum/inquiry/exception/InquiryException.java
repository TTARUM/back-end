package com.ttarum.inquiry.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatusCode;

public class InquiryException extends TtarumException {

    public InquiryException(final HttpStatusCode status, final String message) {
        super(status, message);
    }

    public InquiryException(final HttpStatusCode status, final String message, final Throwable cause) {
        super(status, message, cause);
    }
}
