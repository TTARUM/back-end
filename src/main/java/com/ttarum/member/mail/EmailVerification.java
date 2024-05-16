package com.ttarum.member.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash
public class EmailVerification {

    private String email;

    @Id
    private String verificationCode;

    private EmailStatus status;
    private String uuid;

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
