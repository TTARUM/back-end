package com.ttarum.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NormalLoginRequest {
    @NotBlank
    // TODO: Add size
    private final String loginId;
    @NotBlank
    // TODO: Add size
    private final String password;
}
