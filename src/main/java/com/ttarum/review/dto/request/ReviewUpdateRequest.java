package com.ttarum.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewUpdateRequest {

    private final String content;
    private final short rating;
}
