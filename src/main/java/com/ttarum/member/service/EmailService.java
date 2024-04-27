package com.ttarum.member.service;

import com.ttarum.member.exception.MemberException;
import com.ttarum.member.mail.exception.MailException;
import com.ttarum.member.repository.NormalMemberRepository;
import com.ttarum.member.repository.OauthMemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.ttarum.member.mail.exception.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final NormalMemberRepository normalMemberRepository;
    private final OauthMemberRepository oauthMemberRepository;
    private final RedisAuthService redisAuthService;

    @Value("${spring.mail.username}")
    private String from;


    public void sendVerificationCodeToRegister(final String email) {
        validateDuplicatingEmail(email);
        String verificationCode = makeVerificationCode();
        // 레디스에 저장

        // 메일 발송
        String title = "[TTARUM]회원가입 인증 메일입니다.";
        String content = "TTARUM 회원가입 인증 메일입니다." +
                "<br><br>" +
                "아래의 번호를 입력해주세요." +
                "<br><br>" +
                "<h2>"
                + verificationCode +
                "<h2/>";
        sendMail(from, email, title, content, verificationCode);
    }

    public void sendMail(final String from, final String to, final String title, final String content, final String verificationCode) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, true);
            javaMailSender.send(message);
            redisAuthService.setDataExpire(verificationCode, to, 60 * 3L);
        } catch (MessagingException e) {
            throw MailException.getInstance(SENDING);
        }
    }

    private void validateDuplicatingEmail(final String email) {
        if (normalMemberRepository.existsByEmail(email) || oauthMemberRepository.existsByEmail(email)) {
            throw new MemberException(HttpStatus.BAD_REQUEST, "중복된 이메일입니다.");
        }
    }

    private String makeVerificationCode() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }

        return randomNumber.toString();
    }

    public boolean checkVerificationCode(final String email, final String verificationCode) {
        if (redisAuthService.getData(verificationCode) == null) {
            return false;
        } else return redisAuthService.getData(verificationCode).equals(email);
    }
}
