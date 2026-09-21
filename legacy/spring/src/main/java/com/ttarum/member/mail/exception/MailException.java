package com.ttarum.member.mail.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatusCode;

import java.util.Objects;

public class MailException extends TtarumException {

    public static MailException getInstance(final ErrorType type) {
        return getInstance(type, null);
    }

    public static MailException getInstance(final ErrorType type, final Throwable throwable) {
        return Objects.isNull(throwable) ? type.createException() : type.createException(throwable);
    }

    protected MailException(final HttpStatusCode status, final String message, final Throwable throwable) {
        super(status, message, throwable);
    }

    protected MailException(final HttpStatusCode status, final String message) {
        super(status, message);
    }

}
