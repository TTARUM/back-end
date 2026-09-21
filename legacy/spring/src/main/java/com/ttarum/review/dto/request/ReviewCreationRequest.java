package com.ttarum.review.dto.request;

import com.ttarum.review.domain.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "리뷰 생성 DTO")
public class ReviewCreationRequest {

    @Schema(description = "주문 Id 값", example = "1")
    private final long orderId;
    @Schema(description = "제품 Id 값", example = "12")
    private final long itemId;
    @Schema(description = "리뷰 제목", example = "좋아요")
    private final String title;
    @Schema(description = "리뷰 내용", example = "와인 좋아요.")
    private final String content;
    @Schema(description = "리뷰 평점", example = "2")
    private final Short rating;


    public Review toReviewEntity() {
        return Review.builder()
                .title(title)
                .content(content)
                .star(rating)
                .build();
    }
}
