package com.ttarum.review.domain;

import com.ttarum.review.dto.request.ReviewUpdateRequest;
import com.ttarum.review.exception.ReviewException;
import com.ttarum.review.validator.ReviewValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class ReviewTest {

    ReviewValidator reviewValidator;

    @BeforeEach
    void setUp() {
        reviewValidator = new ReviewValidator();
    }

    @Test
    @DisplayName("업데이트")
    void update() {
        // given
        Review review = Review.builder()
                .content("Content before updating.")
                .star(Integer.valueOf(3).shortValue())
                .build();
        ReviewUpdateRequest updateRequest = ReviewUpdateRequest.builder()
                .content("Content after updating.")
                .rating(Integer.valueOf(4).shortValue())
                .build();

        // when
        review.update(updateRequest, reviewValidator);

        // then
        assertThat(review.getContent()).isEqualTo(updateRequest.getContent());
        assertThat(review.getStar()).isEqualTo(updateRequest.getRating());
    }

    @ParameterizedTest
    @ValueSource(shorts = {-1, 6})
    @DisplayName("업데이트 - 올바르지 않은 평점의 경우 예외가 발생한다.")
    void updateFailedByInvalidRating(final short input) {
        // given
        Review review = Review.builder()
                .content("Content before updating.")
                .star(Integer.valueOf(3).shortValue())
                .build();
        ReviewUpdateRequest updateRequest = ReviewUpdateRequest.builder()
                .content("Content after updating.")
                .rating(input)
                .build();

        // when & then
        assertThatThrownBy(() -> review.update(updateRequest, reviewValidator))
                .isInstanceOf(ReviewException.class);
    }
}