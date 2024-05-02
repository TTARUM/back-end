package com.ttarum.review.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    void findReviewResponseByItemId() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        reviewRepository.findReviewResponseByItemId(1L, pageRequest);
    }

    @Test
    void findReviewResponseByMemberId() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        reviewRepository.findReviewResponseByMemberId(1L, pageRequest);
    }
}