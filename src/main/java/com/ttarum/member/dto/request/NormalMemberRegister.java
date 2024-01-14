package com.ttarum.member.dto.request;

import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import lombok.Builder;

@Builder
public class NormalMemberRegister {
    private String name;
    private String nickname;
    private String phoneNumber;
    private String loginId;
    private String password;
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
