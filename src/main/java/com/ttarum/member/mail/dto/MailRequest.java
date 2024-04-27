package com.ttarum.member.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailRequest {

    @Email
    @NotEmpty
    private String email;
}
