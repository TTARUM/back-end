package com.ttarum.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private final String nickname;
    private final String content;
    private final short rating;
    private final String imageUrl;
    private final Instant createdAt;

}
