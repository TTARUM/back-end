package com.ttarum.member.exception;

import com.ttarum.common.exception.TtarumException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class AddressException extends TtarumException {

    private static final String NOT_FOUND_MESSAGE = "해당 Address ID로 배송지를 찾을 수 없습니다.";
    private static final String NO_OWNER_MESSAGE = "해당 배송지의 소유자가 아닙니다.";

    public static AddressException NotFound() {
        return new AddressException(HttpStatus.BAD_REQUEST, NOT_FOUND_MESSAGE);
    }

    public static AddressException NoOwner() {
        return new AddressException(HttpStatus.BAD_REQUEST, NO_OWNER_MESSAGE);
    }

    public AddressException(final HttpStatusCode status, final String message) {
        super(status, message);
    }
}
