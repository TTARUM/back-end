package com.ttarum.member.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class EmailCheckDTO {

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String verificationCode;
}
