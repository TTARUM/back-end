package com.ttarum.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "장바구니 조회 DTO")
public class CartResponse {

    @Schema(description = "제품의 Id 값", example = "1")
    private final long itemId;

    @Schema(description = "제품의 이름", example = "맛있는 와인")
    private final String itemName;

    @Schema(description = "제품의 카테고리 이름", example = "레드 와인")
    private final String categoryName;

    @Schema(description = "제품의 이미지 URL", example = "ttarum.itemName.url")
    private final String itemImageUrl;

    @Schema(description = "제품의 가격", example = "16500")
    private final int price;

    @Schema(description = "장바구니에 담긴 해당 제품의 개수", example = "7")
    private final int amount;
}
