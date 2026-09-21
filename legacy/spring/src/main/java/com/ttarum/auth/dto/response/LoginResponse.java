package com.ttarum.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {

    @Schema(description = "회원의 이름", example = "홍길동")
    private final String name;

    @Schema(description = "회원의 닉네임", example = "와인조아")
    private final String nickname;

    @Schema(description = "회원 프로필 이미지 URl", example = "ttarum.user_image.url")
    private final String imageUrl;

    @Schema(description = "회원의 전화번호", example = "010-0000-0000")
    private final String phoneNumber;

    @Schema(description = "Access Token", example = "asdf.asdf.asdf")
    private final String token;
}
