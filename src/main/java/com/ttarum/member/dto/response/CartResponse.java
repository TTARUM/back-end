package com.ttarum.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartResponse {

    private final long itemId;
    private final String itemName;
    private final String categoryName;
    private final String itemImageUrl;
    private final int price;
}
