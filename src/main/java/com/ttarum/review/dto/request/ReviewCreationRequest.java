package com.ttarum.review.dto.request;

import com.ttarum.review.domain.Review;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewCreationRequest {

    private final long orderId;
    private final long itemId;
    private final String title;
    private final String content;
    private final Short rating;


    public Review toReviewEntity() {
        return Review.builder()
                .title(title)
                .content(content)
                .star(rating)
                .build();
    }
}
