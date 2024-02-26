package com.ttarum.common.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Schema(description = "모든 올바르지 않은 요청에 대한 응답 DTO")
public class ErrorResponse {

    @Schema(description = "요청 일자 & 시각", example = "2024-02-23T04:32:49.584863Z")
    private final Instant dateTime;

    @Schema(description = "오류 메시지", example = "요청이 정상적으로 처리되지 않았습니다.")
    private final String message;

    public static ErrorResponse generate(final Instant dateTime, final String message) {
        return new ErrorResponse(dateTime, message);
    }
}
