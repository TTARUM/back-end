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

    @Schema(description = "업데이트할 리뷰의 내용")
    private final String content;
}
