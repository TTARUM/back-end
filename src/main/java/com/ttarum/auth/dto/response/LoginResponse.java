package com.ttarum.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final String name;
    private final String nickname;
    private final String imageUrl;
    private final String phoneNumber;
    private final String token;
}
