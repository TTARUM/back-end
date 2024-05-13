package com.ttarum.member.mail;

import lombok.Getter;

@Getter
public class EmailVerification {

    private final String email;

    private final String verificationCode;

    private EmailStatus status;
    private final String uuid;

    public EmailVerification(final String email, final String verificationCode, final String uuid) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.status = EmailStatus.INVALID;
        this.uuid = uuid;
    }

    public boolean verify(final String email, final String verificationCode) {
        if (email.equals(this.email) && verificationCode.equals(this.verificationCode)) {
            this.status = EmailStatus.VALID;
            return true;
        }
        return false;
    }
}
