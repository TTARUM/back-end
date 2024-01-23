package com.ttarum.member.dto.request;

import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(name = "NormalMemberRegister", description = "일반유저 회원가입 DTO")
public class NormalMemberRegister {
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @Schema(description = "닉네임", example = "와인조아")
    private String nickname;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;
    @Schema(description = "로그인 아이디", example = "ttarum")
    private String loginId;
    @Schema(description = "비밀번호", example = "password1234")
    private String password;
    @Schema(description = "이메일", example = "ttarum@gmail.com")
    private String email;

    public Member toMemberEntity() {
        return Member.builder()
                .name(this.name)
                .nickname(this.nickname)
                .phoneNumber(this.phoneNumber)
                .build();
    }

    public NormalMember toNormalMemberEntity() {
        return NormalMember.builder()
                .loginId(this.loginId)
                .password(this.password)
                .email(this.email)
                .build();
    }
}
