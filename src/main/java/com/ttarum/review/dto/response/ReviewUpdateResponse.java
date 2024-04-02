package com.ttarum.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Schema(description = "리뷰 업데이트를 위한 조회 DTO")
public class ReviewUpdateResponse {

    @Schema(description = "제품의 이름", example = "모스카토 다스티")
    private final String itemName;
    @Schema(description = "리뷰 내용", example = "듣던대로 맛 좋네요 ㅎㅎ")
    private final String content;
    @Schema(description = "리뷰 생성일", example = "2024-02-23T04:32:49.584863Z")
    private final Instant createdAt;
    @Schema(description = "리뷰 이미지 URL 리스트")
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
