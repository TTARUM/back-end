package com.ttarum.order.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void findPopularItemIdsByInstant() {
        // given
        Instant after = Instant.now();
        Instant before = after.minus(7, ChronoUnit.DAYS);
        Long categoryId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 7);

        // when
        orderRepository.getPopularItemIdsByInstant(before, after, categoryId, pageRequest);
    }
}
