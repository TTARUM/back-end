package com.ttarum.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ItemSummaryResponseForWishList {

    private final long id;
    private final String categoryName;
    private final String name;
    private final int price;
    private final double rating;
    private final String imageUrl;
    private final Instant createdAt;
}
