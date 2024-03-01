package com.ttarum.member.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class WishlistRepositoryTest {

    @Autowired
    WishlistRepository wishListRepository;

    @Test
    void findItemSummaryByMemberId() {
        long memberId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 8);
        wishListRepository.findItemSummaryByMemberId(memberId, pageRequest);
    }
}
