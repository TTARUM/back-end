package com.ttarum.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "제품 상세보기 DTO")
public class ItemDetailResponse {

    @Schema(description = "제품 이름", example = "베반 셀러스 온토게니")
    private final String name;

    @Schema(description = "제품 설명", example = "Jeb Dunnuck 나파 밸리 100 달러 미만 전체 1위에 해당 되는 빈티지 와인이다. (3,952 케이스 생산)")
    private final String description;

    @Schema(description = "제품 가격", example = "199000")
    private final int price;

    @Schema(description = "제품 미리보기 이미지", example = "ttarum.image.url")
    private final String imageUrl;

    @Schema(description = "제품 설명 이미지", example = "ttarum.description_image.url")
    private final String descriptionImageUrl;
}
