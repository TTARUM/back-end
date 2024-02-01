package com.ttarum.common.dto.response.error;

import lombok.Getter;

import java.time.Instant;

@Getter
public class TypeMismatchExceptionResponse extends ErrorResponse {

    private final String property;
    private final String requiredType;
    private final String actualType;

    protected TypeMismatchExceptionResponse(final Instant dateTime, final String message, final String property, final String requiredType, final String actualType) {
        super(dateTime, message);
        this.property = property;
        String[] requiredTypeSplit = requiredType.split("\\.");
        this.requiredType = requiredTypeSplit[requiredTypeSplit.length - 1];
        String[] actualTypeSplit = actualType.split("\\.");
        this.actualType = actualTypeSplit[actualTypeSplit.length - 1];
    }

    public static TypeMismatchExceptionResponse generate(final Instant now, final String message, final String property, final String requiredType, final String actualType) {
        return new TypeMismatchExceptionResponse(now, message, property, requiredType, actualType);
    }
}
