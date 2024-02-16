package com.ttarum.member.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class MemberNotFoundException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "";

    public MemberNotFoundException() {
        super(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE);
    }

    public MemberNotFoundException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
