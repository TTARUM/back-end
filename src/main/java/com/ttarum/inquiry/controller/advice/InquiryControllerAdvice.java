package com.ttarum.inquiry.controller.advice;

import com.ttarum.common.dto.response.error.ErrorResponse;
import com.ttarum.inquiry.exception.InquiryException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Hidden
@RestControllerAdvice(basePackages = {"com.ttarum.inquiry"})
public class InquiryControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InquiryException.class)
    public ErrorResponse inquiryException(final InquiryException e) {
        return ErrorResponse.generate(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse unknownException(final Exception e) {
        log.error("Error", e);
        return ErrorResponse.generate("서버 내부 오류");
    }
}
