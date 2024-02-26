package com.ttarum.review.dto.response;

import com.ttarum.review.domain.ReviewImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "리뷰 이미지 DTO")
public class ReviewImageResponse {

    @Schema(description = "이미지 순서", example = "1")
    private final int order;

    @Schema(description = "이미지 URL", example = "ttarum.image.url")
    private final String imageUrl;

    public static ReviewImageResponse of(ReviewImage reviewImage) {
        return new ReviewImageResponse(reviewImage.getOrder(), reviewImage.getFileUrl());
    }
}
