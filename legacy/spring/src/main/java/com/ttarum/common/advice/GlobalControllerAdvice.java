package com.ttarum.common.advice;

import com.ttarum.common.dto.response.error.ErrorResponse;
import com.ttarum.common.exception.TtarumException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.http.HttpStatusCode;

import java.time.Instant;

@Slf4j
@RestControllerAdvice(basePackages = {"com.ttarum"})
public class GlobalControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentException(final MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatusCode.BAD_REQUEST)
                .body(ErrorResponse.generate(Instant.now(), "잘못된 파라미터 요청입니다. Request body를 확인해주세요: " + e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentException(final IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatusCode.BAD_REQUEST)
                .body(ErrorResponse.generate(Instant.now(), "잘못된 파라미터 요청입니다. Request body를 확인해주세요: " + e.getMessage()));
    }

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
