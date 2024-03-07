package com.ttarum.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemSummary {

    private final long itemId;
    private final String itemImageUrl;
    private final String itemName;
    private final int itemPrice;
    private final long amount;
    private final boolean hasReview;
}
