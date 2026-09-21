package com.ttarum.member.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class EmailCheckDTO {

    @Email
    @NotEmpty
    @Schema(description = "이메일", example = "asdf@asdf.com")
    private String email;

    @NotEmpty
    @Schema(description = "인증 코드", example = "123456")
    private String verificationCode;
}
