package com.ttarum.common.advice;

import com.ttarum.common.dto.response.error.ErrorResponse;
import com.ttarum.common.dto.response.error.TypeMismatchExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(basePackages = {"com.ttarum"})
public class CommonControllerAdvice {

    @SuppressWarnings("null")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public ErrorResponse typeMismatchExceptionHandler(final TypeMismatchException e) {
        return TypeMismatchExceptionResponse.generate(
                Instant.now(),
                "TypeMismatch",
                e.getPropertyName(),
                Objects.nonNull(e.getRequiredType()) ? e.getRequiredType().getTypeName() : "",
                Objects.nonNull(e.getValue())? e.getValue().getClass().getName() : ""
        );
    }
}
