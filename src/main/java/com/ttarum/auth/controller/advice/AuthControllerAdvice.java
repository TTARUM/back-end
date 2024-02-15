package com.ttarum.auth.controller.advice;

import com.ttarum.auth.exception.AuthException;
import com.ttarum.common.dto.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Hidden
@Slf4j
@RestControllerAdvice(basePackages = "com.ttarum.auth")
public class AuthControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthException.class)
    public ErrorResponse authException(final AuthException e) {
        return ErrorResponse.generate(Instant.now(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse unknownException(final Exception e) {
        log.error("Error", e);
        return ErrorResponse.generate(Instant.now(), "서버 내부 오류");
    }
}
