package com.ttarum.member.service;

import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import com.ttarum.member.exception.MemberException;
import com.ttarum.member.repository.MemberRepository;
import com.ttarum.member.repository.NormalMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final NormalMemberRepository normalMemberRepository;

    public void registerNormalUser(Member member, NormalMember normalMember) throws MemberException {
        if (isNicknameDuplicate(member.getNickname())) {
            throw new MemberException("닉네임이 중복되었습니다.");
        }
        memberRepository.save(member);
        normalMemberRepository.save(normalMember);
    }

    public boolean isNicknameDuplicate(final String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }
}
