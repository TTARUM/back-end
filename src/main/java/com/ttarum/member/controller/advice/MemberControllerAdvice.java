package com.ttarum.member.controller.advice;

import com.ttarum.common.dto.response.error.ErrorResponse;
import com.ttarum.member.exception.MemberException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@Hidden
@RestControllerAdvice(basePackages = "com.ttarum.member")
public class MemberControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MemberException.class)
    public ErrorResponse itemException(final MemberException e) {
        return ErrorResponse.generate(Instant.now(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse unknownException(final Exception e) {
        log.error("Error", e);
        return ErrorResponse.generate(Instant.now(), "서버 내부 오류");
    }
}
