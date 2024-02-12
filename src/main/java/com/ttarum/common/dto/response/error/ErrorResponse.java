package com.ttarum.common.dto.response.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ErrorResponse {

    private final Instant dateTime;
    private final String message;

    public static ErrorResponse generate(final Instant dateTime, final String message) {
        return new ErrorResponse(dateTime, message);
    }
}
