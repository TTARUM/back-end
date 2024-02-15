package com.ttarum.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class TtarumException extends ResponseStatusException {

    private final String message;

    public TtarumException(final HttpStatusCode status, final String message) {
        super(status, message);
        this.message = message;
    }
}
