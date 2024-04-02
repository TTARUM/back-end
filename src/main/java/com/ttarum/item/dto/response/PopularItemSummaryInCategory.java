package com.ttarum.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "인기상품 조회의 상품 DTO")
public class PopularItemSummaryInCategory {

    @Schema(description = "제품 Id 값", example = "3")
    private final long itemId;

    @Schema(description = "제품 이름", example = "모스카토 다스티")
    private final String itemName;

    @Schema(description = "제품의 가격", example = "22500")
    private final int price;

    @Schema(description = "제품의 이미지 URL", example = "ttarum.image.url")
    private final String imageUrl;

    @Schema(description = "찜목록 포함 여부", example = "true")
    private final boolean inWishlist;
}
