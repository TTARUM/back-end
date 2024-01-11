package com.ttarum.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemSummaryResponse {

    private final long id;
    private final String categoryName;
    private final String name;
    private final int price;
    private final double rating;
    private final String imageUrl;
}
