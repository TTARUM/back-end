package com.ttarum.member.mail;

import lombok.Getter;

import java.time.Instant;

@Getter
public class VerificationCode {

    private final String code;
    private Status status;
    private final Instant datetime;

    public VerificationCode(final String code, final Instant datetime) {
        this.code = code;
        this.datetime = datetime;
        this.status = Status.INVALID;
    }

    enum Status {
        VALID, INVALID
    }

}
