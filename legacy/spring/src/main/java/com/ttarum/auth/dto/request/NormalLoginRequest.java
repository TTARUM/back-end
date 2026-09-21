package com.ttarum.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 DTO")
public class NormalLoginRequest {
    @Schema(description = "로그인 ID", example = "login1234")
    @NotBlank
    // TODO: Add size
    private final String loginId;

    @Schema(description = "비밀번호", example = "password1234")
    @NotBlank
    // TODO: Add size
    private final String password;
}
