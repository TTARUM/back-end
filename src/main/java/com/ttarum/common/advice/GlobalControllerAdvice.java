package com.ttarum.common.advice;

import com.ttarum.common.dto.response.error.ErrorResponse;
import com.ttarum.common.exception.TtarumException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.http.HttpStatusCode;

import java.time.Instant;

@Slf4j
@RestControllerAdvice(basePackages = {"com.ttarum"})
public class GlobalControllerAdvice {

    @ExceptionHandler(TtarumException.class)
    public ResponseEntity<ErrorResponse> globalException(final TtarumException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(ErrorResponse.generate(Instant.now(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unknownException(final Exception e) {
        log.error("Error", e);
        return ResponseEntity.status(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.generate(Instant.now(), "서버 내부 오류"));
    }
}
