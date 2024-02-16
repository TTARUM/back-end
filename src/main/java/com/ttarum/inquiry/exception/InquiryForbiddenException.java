package com.ttarum.inquiry.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class InquiryForbiddenException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "해당 문의글을 열람할 권한이 없습니다.";

    public InquiryForbiddenException() {
        super(HttpStatus.FORBIDDEN, DEFAULT_MESSAGE);
    }

    public InquiryForbiddenException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
