package com.ttarum.auth.controller.advice;

import com.ttarum.auth.exception.AuthException;
import com.ttarum.common.dto.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice(basePackages = "com.ttarum.auth")
public class AuthControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthException.class)
    public ErrorResponse authException(final AuthException e) {
        return ErrorResponse.generate(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse unknownException(final Exception e) {
        return ErrorResponse.generate("서버 내부 오류");
    }
}
