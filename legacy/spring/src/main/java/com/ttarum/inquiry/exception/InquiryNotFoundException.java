package com.ttarum.inquiry.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class InquiryNotFoundException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "해당 문의글이 존재하지 않습니다.";

    public InquiryNotFoundException() {
        super(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE);
    }

    public InquiryNotFoundException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
