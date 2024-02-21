package com.ttarum.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "찜 목록 조회에 대한 응답")
public class ItemSummaryResponseForWishList {

    @Schema(description = "제품의 Id 값")
    private final long itemId;

    @Schema(description = "제품의 이름")
    private final String name;

    @Schema(description = "카테고리")
    private final String categoryName;

    @Schema(description = "제품의 가격")
    private final int price;

    @Schema(description = "제품의 평점")
    private final double rating;

    @Schema(description = "제품 이미지 url")
    private final String imageUrl;

    @Schema(description = "찜 목록에 추가된 날짜")
    private final Instant createdAt;
}
