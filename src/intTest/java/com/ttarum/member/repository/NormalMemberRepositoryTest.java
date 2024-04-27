package com.ttarum.member.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class NormalMemberRepositoryTest {

    @Autowired
    NormalMemberRepository normalMemberRepository;

    @Test
    void existsByEmail() {
        String email = "a@google.com";
        normalMemberRepository.existsByEmail(email);
    }
}