package com.ttarum.member.repository;

import com.ttarum.member.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveMember() {
        Member member = Member.builder()
                .name("testName")
                .nickname("foo")
                .phoneNumber("testPhoneNumber")
                .build();

        Member saved = memberRepository.save(member);

        assertEquals("testName", saved.getName());
    }
}
