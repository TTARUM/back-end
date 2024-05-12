package com.ttarum.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "찜 목록 조회 시 제품 DTO")
public class ItemSummaryResponseForWishlist {

    @Schema(description = "제품의 Id 값", example = "1")
    private Long itemId;

    @Schema(description = "제품의 이름", example = "베반 셀러스 온토게니")
    private String name;

    @Schema(description = "제품의 카테고리 이름", example = "레드 와인")
    private String categoryName;

    @Schema(description = "제품의 가격", example = "199000")
    private Integer price;

    @Schema(description = "제품의 평점", example = "4.5")
    private Double rating;

    @Schema(description = "제품 이미지 url", example = "ttarum.image.url")
    private String imageUrl;

    @Schema(description = "찜 목록에 추가된 날짜", example = "2024-02-23T04:32:49.584863Z")
    private Instant createdAt;
}
