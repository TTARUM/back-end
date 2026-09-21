package com.ttarum.member.mail.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    SENDING {
        @Override
        MailException createException(final Throwable throwable) {
            return new MailException(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패했습니다.", throwable);
        }

        @Override
        MailException createException() {
            return new MailException(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패했습니다.");
        }
    }, VALIDATING {
        @Override
        MailException createException(final Throwable throwable) {
            return new MailException(HttpStatus.BAD_REQUEST, "인증에 실패했습니다.", throwable);
        }

        @Override
        MailException createException() {
            return new MailException(HttpStatus.BAD_REQUEST, "인증에 실패했습니다.");
        }
    };

    abstract MailException createException(final Throwable throwable);
    abstract MailException createException();
}
