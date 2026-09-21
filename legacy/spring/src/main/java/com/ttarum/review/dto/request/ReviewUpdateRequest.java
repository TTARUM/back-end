package com.ttarum.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "리뷰 업데이트 DTO")
public class ReviewUpdateRequest {

    @Schema(description = "리뷰의 변경될 내용")
    private final String content;
    @Schema(description = "리뷰의 변경될 평점")
    private final short rating;
}
