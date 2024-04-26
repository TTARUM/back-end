package com.ttarum.member.mail;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class CodeStore {

    private static Map<String, VerificationCode> codeMap = new HashMap<>();

    public void saveCode(final String verificationCode) {
        VerificationCode code = new VerificationCode(verificationCode, Instant.now());
        codeMap.put(verificationCode, code);
    }
}
