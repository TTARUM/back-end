package com.ttarum.member.mail.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindingEmailRequest {

    private String name;
    private String email;
    private String verificationCode;
    private String sessionId;
}
