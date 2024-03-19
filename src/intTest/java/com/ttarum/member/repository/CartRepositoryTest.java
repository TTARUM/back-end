package com.ttarum.member.repository;

import com.ttarum.member.domain.CartId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

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

    @Test
    void deleteAllByIdList() {
        // given
        long memberId = 1L;
        List<CartId> cartIdList = List.of(new CartId(memberId, 1L), new CartId(memberId, 2L));

        // when
        cartRepository.deleteAllByIdList(cartIdList);
    }
}
