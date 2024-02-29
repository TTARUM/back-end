package com.ttarum.member.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class DuplicatedWishlistException extends TtarumException {

    private static final String DEFAULT_MESSAGE = "해당 제품이 이미 찜 목록에 존재합니다.";

    public DuplicatedWishlistException() {
        super(HttpStatus.BAD_REQUEST, DEFAULT_MESSAGE);
    }

    public DuplicatedWishlistException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
