package com.ttarum.item.controller.advice;

import com.ttarum.common.dto.response.error.ErrorResponse;
import com.ttarum.item.exception.ItemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.ttarum.item")
public class ItemControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ItemException.class)
    public ErrorResponse itemException(final ItemException e) {
        return ErrorResponse.generate(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse unknownException(final Exception e) {
        log.error("Error", e);
        return ErrorResponse.generate("서버 내부 오류");
    }
}
