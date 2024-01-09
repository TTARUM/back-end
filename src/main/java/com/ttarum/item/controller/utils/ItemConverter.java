package com.ttarum.item.controller.utils;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemDetailResponse;

public class ItemConverter {

    public static ItemDetailResponse convertToDetailResponse(final Item item) {
        return ItemDetailResponse.builder()
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getItemImageUrl())
                .descriptionImageUrl(item.getItemDescriptionImageUrl())
                .build();
    }
}
