package com.ttarum.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "카테고리 인기상품 DTO")
public class PopularItemInCategoryResponse {

    @Schema(description = "인기상품 리스트")
    private final List<PopularItemSummaryInCategory> itemSummaryList;
}
