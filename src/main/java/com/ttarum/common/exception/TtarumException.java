package com.ttarum.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class TtarumException extends ResponseStatusException {
    private final HttpStatusCode status;
    private final String message;

    public TtarumException(final HttpStatusCode status, final String message) {
        super(status, message);
        this.status = status;
        this.message = message;
    }
    public TtarumException(final HttpStatusCode status, final String message, final Throwable throwable) {
        super(status, message, throwable);
        this.status = status;
        this.message = message;
    }
}
