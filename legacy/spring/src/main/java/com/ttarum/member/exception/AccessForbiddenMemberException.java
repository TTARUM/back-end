package com.ttarum.member.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class AccessForbiddenMemberException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "접근이 금지된 회원입니다.";

    public AccessForbiddenMemberException() {
        super(HttpStatus.FORBIDDEN, DEFAULT_MESSAGE);
    }

    public AccessForbiddenMemberException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
