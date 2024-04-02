package com.ttarum.common.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.Instant;

@Getter
@Schema(description = "요청에 필요한 파라미터 누락 시 응답 DTO")
public class MissingServletRequestParameterExceptionResponse extends ErrorResponse {

    @Builder
    protected MissingServletRequestParameterExceptionResponse(final Instant dateTime, final String message) {
        super(dateTime, message);
    }

    public static MissingServletRequestParameterExceptionResponse generate(final Instant now, final MissingServletRequestParameterException e) {
        String message = "%s타입의 %s 값은 필수입니다.".formatted(e.getParameterType(), e.getParameterName());
        return MissingServletRequestParameterExceptionResponse.builder()
                .dateTime(now)
                .message(message)
                .build();
    }
}
