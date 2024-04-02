package com.ttarum.common.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Schema(description = "요청에 필요한 파라미터의 타입이 잘못된 경우의 응답 DTO")
public class TypeMismatchExceptionResponse extends ErrorResponse {

    @Schema(description = "파라미터 이름", example = "reviewId")
    private final String property;

    @Schema(description = "요구되는 타입", example = "long")
    private final String requiredType;

    @Schema(description = "요청에 사용된 타입", example = "String")
    private final String actualType;

    @Builder
    protected TypeMismatchExceptionResponse(final Instant dateTime, final String message, final String property, final String requiredType, final String actualType) {
        super(dateTime, message);
        this.property = property;
        String[] requiredTypeSplit = requiredType.split("\\.");
        this.requiredType = requiredTypeSplit[requiredTypeSplit.length - 1];
        String[] actualTypeSplit = actualType.split("\\.");
        this.actualType = actualTypeSplit[actualTypeSplit.length - 1];
    }

    public static TypeMismatchExceptionResponse generate(final Instant now, final String message, final String property, final String requiredType, final String actualType) {
        return TypeMismatchExceptionResponse.builder()
                .dateTime(now)
                .message(message)
                .property(property)
                .requiredType(requiredType)
                .actualType(actualType)
                .build();
    }
}
