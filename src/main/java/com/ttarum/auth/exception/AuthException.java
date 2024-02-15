package com.ttarum.auth.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatusCode;

public class AuthException extends TtarumException {

    public AuthException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
