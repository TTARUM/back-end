package com.ttarum.member.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatusCode;

public class MemberException extends TtarumException {

    public MemberException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
