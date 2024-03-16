package com.ttarum.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "가격대가 비슷한 제품 DTO")
public class ItemSimilarPriceResponse {

    @Schema(description = "제품들의 리스트")
    private final List<ItemSummaryWithSimilarPrice> itemSummaryList;
}
