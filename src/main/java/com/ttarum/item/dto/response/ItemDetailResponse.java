package com.ttarum.item.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemDetailResponse {

    private final String name;
    private final String description;
    private final int price;
    private final String imageUrl;
    private final String descriptionImageUrl;
}
