package com.ttarum.member.service;

import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import com.ttarum.member.exception.MemberException;
import com.ttarum.member.repository.MemberRepository;
import com.ttarum.member.repository.NormalMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private NormalMemberRepository normalMemberRepository;

    @Test
    @DisplayName("일반 유저 회원가입 - happy path")
    void registerNormalUser() {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .email("testEmail@gmail.com")
                .build();

        // when
        assertDoesNotThrow(() -> memberService.registerNormalUser(targetMember, targetNormalMember));

        // then
        verify(memberRepository, times(1)).save(targetMember);
        verify(normalMemberRepository, times(1)).save(targetNormalMember);
    }

    @ParameterizedTest
    @DisplayName("일반 유저 회원가입 - 유효하지 않은 닉네임으로 가입 시 예외가 발생한다.")
    @ValueSource(strings = {"", "tooLongNickname"})
    void registerNormalUser_registerWithInvalidNicknameThrowException(String nickname) {
        // given
        Member targetMember = Member.builder()
                .nickname(nickname)
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .email("testEmail@gmail.com")
                .build();

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("올바르지 않은 닉네임입니다."));
    }

    @ParameterizedTest
    @DisplayName("일반 유저 회원가입 - 유효하지 않은 이메일로 가입 시 예외가 발생한다.")
    @ValueSource(strings = {"", "noDomain", "noAtSign.com", "noDot@com"})
    void registerNormalUser_registerWithInvalidEmailThrowException(String email) {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .email(email)
                .build();

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("올바르지 않은 이메일입니다."));
    }

    @ParameterizedTest
    @DisplayName("일반 유저 회원가입 - 유효하지 않은 비밀번호로 가입 시 예외가 발생한다.")
    @ValueSource(strings = {"", "short", "noNumber", "12341234"})
    void registerNormalUser_registerWithInvalidPasswordThrowException(String password) {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password(password)
                .email("testEmail@gmail.com")
                .build();

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("올바르지 않은 비밀번호입니다."));
    }

    @Test
    @DisplayName("일반 유저 회원가입 - 닉네임 중복 시 예외가 발생한다.")
    void registerNormalUser_registerWithDuplicateNicknameThrowException() {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .email("testEmail@gmail.com")
                .build();

        when(memberRepository.findByNickname(anyString())).thenReturn(Optional.of(targetMember));

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("닉네임이 중복되었습니다."));
    }

    @Test
    @DisplayName("일반 유저 회원가입 - 로그인 아이디 중복 시 예외가 발생한다.")
    void registerNormalUser_registerWithDuplicateLoginIdThrowException() {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .email("testEmail@gmail.com")
                .build();

        when(normalMemberRepository.findNormalMemberByLoginId("testLoginId")).thenReturn(Optional.of(targetNormalMember));

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("로그인 아이디가 중복되었습니다."));
    }
}
