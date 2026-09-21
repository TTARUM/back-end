package com.ttarum.auth.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class AuthException extends TtarumException {
    private static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    private static final String INVALID_PASSWORD = "비밀번호가 일치하지 않습니다.";
    private static final String INVALID_TOKEN = "유효하지 않은 토큰입니다.";
    private static final String DELETED_MEMBER = "탈퇴한 회원입니다.";

    public AuthException(final HttpStatusCode status, final String message) {
        super(status, message);
    }

    public static AuthException UserNotFound() {
        return new AuthException(HttpStatus.UNAUTHORIZED, USER_NOT_FOUND);
    }

    public static AuthException InvalidPassword() {
        return new AuthException(HttpStatus.UNAUTHORIZED, INVALID_PASSWORD);
    }

    public static AuthException InvalidToken() {
        return new AuthException(HttpStatus.UNAUTHORIZED, INVALID_TOKEN);
    }

    public static AuthException DeletedMember() {
        return new AuthException(HttpStatus.UNAUTHORIZED, DELETED_MEMBER);
    }
}
