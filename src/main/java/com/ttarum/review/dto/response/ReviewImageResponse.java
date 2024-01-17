package com.ttarum.review.dto.response;

import com.ttarum.review.domain.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewImageResponse {

    private final int order;
    private final String imageUrl;

    public static ReviewImageResponse of(ReviewImage reviewImage) {
        return new ReviewImageResponse(reviewImage.getOrder(), reviewImage.getFileUrl());
    }
}
