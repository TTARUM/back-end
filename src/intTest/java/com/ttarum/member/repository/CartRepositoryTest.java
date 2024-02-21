package com.ttarum.member.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    CartRepository cartRepository;

    @Test
    void getCartListResponse() {
        // given
        long memberId = 1L;

        // when
        cartRepository.getCartResponseListByMemberId(memberId);
    }
}
