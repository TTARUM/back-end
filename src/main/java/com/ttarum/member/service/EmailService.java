package com.ttarum.member.service;

import com.ttarum.member.exception.MemberException;
import com.ttarum.member.mail.CodeStore;
import com.ttarum.member.repository.NormalMemberRepository;
import com.ttarum.member.repository.OauthMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final NormalMemberRepository normalMemberRepository;
    private final OauthMemberRepository oauthMemberRepository;
    private final CodeStore codeStore;


    public void sendVerificationCodeToRegister(final String email) {
        validateDuplicatingEmail(email);
        if (!isValidEmail(email)) {
            throw new MemberException(HttpStatus.BAD_REQUEST, "올바르지 않은 이메일입니다.");
        }
        String verificationCode = makeVerificationCode();
        // 세션 생성 & 세션에 코드 저장
        codeStore.saveCode(verificationCode);
        // 메일 발송
    }

    private void validateDuplicatingEmail(final String email) {
        if (normalMemberRepository.existsByEmail(email) || oauthMemberRepository.existsByEmail(email)) {
            throw new MemberException(HttpStatus.BAD_REQUEST, "중복된 이메일입니다.");
        }
    }

    private boolean isValidEmail(final String email) {
        if (email.isEmpty() || email.length() > 320) {
            return false;
        }
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    private String makeVerificationCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0:
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                default:
                    key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }
}
