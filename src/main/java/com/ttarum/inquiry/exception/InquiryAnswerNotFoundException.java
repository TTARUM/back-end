package com.ttarum.inquiry.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class InquiryAnswerNotFoundException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "문의 답변이 존재하지 않습니다.";

    public InquiryAnswerNotFoundException() {
        super(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE);
    }

    public InquiryAnswerNotFoundException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
