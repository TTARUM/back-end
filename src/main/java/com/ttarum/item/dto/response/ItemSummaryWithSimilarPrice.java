package com.ttarum.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ItemSummaryWithSimilarPrice {

    private final long itemId;
    private final String itemName;
    private final int price;
    private final String imageUrl;
    private final boolean inWishList;
}
