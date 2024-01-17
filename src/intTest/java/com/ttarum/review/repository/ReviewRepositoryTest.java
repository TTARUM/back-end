package com.ttarum.review.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    void findReviewResponseByItemId() {
        reviewRepository.findReviewResponseByItemId(1L);
    }

    @Test
    void findReviewResponseByItemIdWithMemberId() {
        reviewRepository.findReviewResponseByItemId(1L, 1L);
    }
}