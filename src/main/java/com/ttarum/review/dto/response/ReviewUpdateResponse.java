package com.ttarum.review.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ReviewUpdateResponse {

    private final String itemName;
    private final String content;
    private final Instant createdAt;
    private final List<String> imageUrlList;

    @Builder
    public ReviewUpdateResponse(final String itemName, final String content, final Instant createdAt) {
        this.itemName = itemName;
        this.content = content;
        this.createdAt = createdAt;
        this.imageUrlList = new ArrayList<>();
    }

    public void addImageUrl(final String imageUrl) {
        this.imageUrlList.add(imageUrl);
    }
}
