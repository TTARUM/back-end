package com.ttarum.member.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailRequest {

    @Email
    @NotEmpty
    @Schema(description = "이메일", example = "asdf@adf.com")
    private String email;
}
