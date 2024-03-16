package com.ttarum.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ItemSimilarPriceResponse {

    private final List<ItemSummaryWithSimilarPrice> itemSummaryList;
}
