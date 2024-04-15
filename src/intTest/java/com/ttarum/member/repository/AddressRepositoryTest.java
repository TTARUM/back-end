package com.ttarum.member.repository;

import com.ttarum.member.domain.Address;
import com.ttarum.member.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class AddressRepositoryTest {
    @Autowired
    AddressRepository addressRepository;

    @Autowired
    MemberRepository memberRepository;

    long testMemberId;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(Member.builder()
                .name("testName")
                .nickname("foo")
                .phoneNumber("testPhoneNumber")
                .build());
        testMemberId = member.getId();
    }

    @AfterEach
    void tearDown() {
        addressRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void findByMemberId() {
        // given
        Member member = memberRepository.findById(testMemberId).get();
        System.out.println(member);
        addressRepository.save(Address.builder()
                .addressAlias("testAlias")
                .recipient("testRecipient")
                .address("testAddress")
                .detailAddress("testDetailAddress")
                .phoneNumber("testPhoneNumber")
                .isDefault(false)
                .member(member)
                .build());

        // when
        List<Address> addresses = addressRepository.findByMemberId(testMemberId);

        // then
        assertEquals(addresses.size(), 1);
    }

    @Test
    void findDefaultAddressByMemberId() {
        // given
        Member member = memberRepository.findById(testMemberId).get();
        addressRepository.save(Address.builder()
                .addressAlias("testAlias")
                .recipient("testRecipient")
                .address("testAddress")
                .detailAddress("testDetailAddress")
                .phoneNumber("testPhoneNumber")
                .isDefault(true)
                .member(member)
                .build());

        // when
        Address address = addressRepository.findDefaultAddressByMemberId(testMemberId).get();

        // then
        assertTrue(address.isDefault());
    }
}
