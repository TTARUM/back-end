package com.ttarum.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "가격대가 비슷한 술에 표현될 데이터가 담긴 DTO")
public class ItemSummaryWithSimilarPrice {

    @Schema(description = "제품의 Id 값", example = "1")
    private final long itemId;
    @Schema(description = "제품의 이름", example = "모스카토 다스티")
    private final String itemName;
    @Schema(description = "가격", example = "22500")
    private final int price;
    @Schema(description = "제품의 이미지 URL", example = "ttarum.image.url")
    private final String imageUrl;
    @Schema(description = "찜 목록 포함 여부", example = "false")
    private final boolean inWishList;
}
