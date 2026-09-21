package com.ttarum.auth.service;

import com.ttarum.auth.componenet.JwtUtil;
import com.ttarum.auth.dto.request.NormalLoginRequest;
import com.ttarum.auth.dto.response.LoginResponse;
import com.ttarum.auth.exception.AuthException;
import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import com.ttarum.member.repository.NormalMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final NormalMemberRepository normalMemberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * 일반 로그인 메서드
     * 로그인에 성공하면 토큰과 함께 로그인한 회원의 정보를 담은 객체를 반환합니다.
     *
     * @param dto 로그인에 필요한 데이터가 담긴 객체
     * @return 회원의 정보와 토큰이 담긴 객체
     * @throws AuthException 해당 회원이 존재하지 않을 경우, 비밀번호가 틀린 경우 발생합니다.
     */
    public LoginResponse normalLogin(final NormalLoginRequest dto) {
        Optional<NormalMember> normalMember = normalMemberRepository.findNormalMemberByLoginId(dto.getLoginId());
        if (normalMember.isEmpty()) {
            throw AuthException.UserNotFound();
        }

        if (normalMember.get().getMember().getIsDeleted()) {
            throw AuthException.DeletedMember();
        }

        if (!passwordEncoder.matches(dto.getPassword(), normalMember.get().getPassword())) {
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
