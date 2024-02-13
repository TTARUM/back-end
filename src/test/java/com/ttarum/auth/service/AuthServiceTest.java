package com.ttarum.auth.service;

import com.ttarum.auth.componenet.JwtUtil;
import com.ttarum.auth.dto.request.NormalLoginRequest;
import com.ttarum.auth.dto.response.LoginResponse;
import com.ttarum.auth.exception.AuthException;
import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import com.ttarum.member.repository.NormalMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private AuthService authService;

    @Mock
    private NormalMemberRepository normalMemberRepository;

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil("secret-key-for-test-010101010101010101010101010");
        authService = new AuthService(normalMemberRepository, jwtUtil);
    }

    @Test
    @DisplayName("일반 유저 로그인 - happy path")
    void normalLogin() {
        // given
        Member targetMember = Member.builder()
                .id(1234L)
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .imageUrl("testImageUrl")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .member(targetMember)
                .build();

        when(normalMemberRepository.findNormalMemberByLoginId("testLoginId")).thenReturn(Optional.of(targetNormalMember));

        NormalLoginRequest normalLoginRequest = NormalLoginRequest.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .build();

        // when
        LoginResponse loginResponse = authService.normalLogin(normalLoginRequest);

        // then
        assertEquals(loginResponse.getName(), targetMember.getName());
        assertEquals(loginResponse.getNickname(), targetMember.getNickname());
        assertEquals(loginResponse.getPhoneNumber(), targetMember.getPhoneNumber());
        assertEquals(loginResponse.getImageUrl(), targetMember.getImageUrl());
    }

    @Test
    @DisplayName("일반 유저 로그인 - 존재하지 않는 아이디로 로그인 시 예외가 발생한다.")
    void normalLogin_loginWithInvalidLoginIdThrowException() {
        // given
        NormalLoginRequest normalLoginRequest = NormalLoginRequest.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .build();

        when(normalMemberRepository.findNormalMemberByLoginId("testLoginId")).thenReturn(Optional.empty());

        // when & then
        assertThrows(AuthException.class, () -> authService.normalLogin(normalLoginRequest));
    }

    @Test
    @DisplayName("일반 유저 로그인 - 비밀번호가 일치하지 않는 경우 예외가 발생한다.")
    void normalLogin_loginWithWrongPasswordThrowException() {
        // given
        Member targetMember = Member.builder()
                .id(1234L)
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .imageUrl("testImageUrl")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .member(targetMember)
                .build();

        when(normalMemberRepository.findNormalMemberByLoginId("testLoginId")).thenReturn(Optional.of(targetNormalMember));

        NormalLoginRequest normalLoginRequest = NormalLoginRequest.builder()
                .loginId("testLoginId")
                .password("!!wrongPassword!!")
                .build();

        assertThrows(AuthException.class, () -> authService.normalLogin(normalLoginRequest));
    }
}