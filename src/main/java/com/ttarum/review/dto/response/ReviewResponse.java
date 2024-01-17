package com.ttarum.review.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Getter
@Builder
public class ReviewResponse {
    private final Long id;
    private final String nickname;
    private final String content;
    private final short rating;
    private final Queue<ReviewImageResponse> imageUrls = new PriorityQueue<>(Comparator.comparingInt(ReviewImageResponse::getOrder));
    private final Instant createdAt;

    public ReviewResponse(final Long id, final String nickname, final String content, final short rating, final Instant createdAt) {
        this.id = id;
        this.nickname = nickname;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public void addImageUrl(final ReviewImageResponse reviewImage) {
        imageUrls.add(reviewImage);
    }
}
