package com.ttarum.member.repository;

import com.ttarum.member.domain.Address;
import com.ttarum.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class AddressRepositoryTest {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("findByMemberIdOrderByLastUsedAtDesc - happy path")
    void findByMemberIdOrderByLastUsedAtDesc_list_is_sorted_by_lastUsedAt() {
        long secForOneDay = 60 * 60 * 24;
        // given
        Member testMember = memberRepository.save(Member.builder()
                .name("testName")
                .nickname("foo")
                .phoneNumber("testPhoneNumber")
                .build());
        addressRepository.save(
                Address.builder()
                        .member(testMember)
                        .address("testAddress")
                        .lastUsedAt(Instant.now().minusSeconds(secForOneDay))
                        .build()
        );
        addressRepository.save(
                Address.builder()
                        .member(testMember)
                        .address("testAddress")
                        .lastUsedAt(Instant.now().minusSeconds(secForOneDay * 2))
                        .build()
        );
        addressRepository.save(
                Address.builder()
                        .member(testMember)
                        .address("testAddress")
                        .lastUsedAt(Instant.now().minusSeconds(secForOneDay * 3))
                        .build()
        );

        // when
        List<Address> list = addressRepository.findByMemberIdOrderByLastUsedAtDesc(testMember.getId());

        // then
        assertEquals(3, list.size());
        for (int i = 0; i < list.size() - 1; i++) {
            assert list.get(i).getLastUsedAt().isAfter(list.get(i + 1).getLastUsedAt());
        }

        // tearDown
        addressRepository.deleteAllById(list.stream().map(Address::getId).toList());
        memberRepository.delete(testMember);
    }
}
