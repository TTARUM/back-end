package com.ttarum.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Getter
@Builder
@Schema(description = "리뷰 조회 DTO")
public class ReviewResponse {

    @Schema(description = "리뷰의 Id 값", example = "1")
    private final Long id;

    @Schema(description = "리뷰 작성자의 닉네임", example = "홍길동")
    private final String nickname;

    @Schema(description = "리뷰 내용", example = "와인 맛있어요.")
    private final String content;

    @Schema(description = "리뷰 평점", example = "4.5")
    private final short rating;

    @Schema(description = "리뷰 이미지 리스트")
    private final Queue<ReviewImageResponse> imageUrls = new PriorityQueue<>(Comparator.comparingInt(ReviewImageResponse::getOrder));

    @Schema(description = "리뷰 생성일", example = "2024-02-23T04:32:49.584863Z")
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
