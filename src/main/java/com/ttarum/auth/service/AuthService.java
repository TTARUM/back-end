package com.ttarum.auth.service;

import com.ttarum.auth.componenet.JwtUtil;
import com.ttarum.auth.dto.request.NormalLoginRequest;
import com.ttarum.auth.dto.response.LoginResponse;
import com.ttarum.auth.exception.AuthException;
import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import com.ttarum.member.repository.NormalMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final NormalMemberRepository normalMemberRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse normalLogin(final NormalLoginRequest dto) {
        Optional<NormalMember> normalMember = normalMemberRepository.findNormalMemberByLoginId(dto.getLoginId());
        if (normalMember.isEmpty()) {
            throw AuthException.UserNotFound();
        }
        // TODO: Use encoded password
        if (!normalMember.get().getPassword().equals(dto.getPassword())) {
            throw AuthException.InvalidPassword();
        }

        Member member = normalMember.get().getMember();
        String accessToken = jwtUtil.generateToken(member.getId());

        return LoginResponse.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .phoneNumber(member.getPhoneNumber())
                .token(accessToken)
                .build();
    }
}
