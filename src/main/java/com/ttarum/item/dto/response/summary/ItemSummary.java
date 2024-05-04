package com.ttarum.item.dto.response.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "제품 미리보기 DTO")
public class ItemSummary {

    @Schema(description = "제품의 Id 값", example = "1")
    private final long id;

    @Schema(description = "제품 카테고리 이름", example = "레드 와인")
    private final String categoryName;

    @Schema(description = "제품 이름", example = "베반 셀러스 온토게니")
    private final String name;

    @Schema(description = "제품 가격", example = "199000")
    private final int price;

    @Schema(description = "제품 평점", example = "4.5")
    private final double rating;

    @Schema(description = "제품 미리보기 이미지 URL", example = "ttarum.image.url")
    private final String imageUrl;

    @Schema(description = "로그인을 했을 경우 해당 제품이 찜 목록에 포함되어있다면 true, 아니면 false, 로그인을 하지 않았다면 모두 false", example = "true")
    private boolean isInWishList;

    @Schema(description = "제품 생성일", example = "2024-02-23T04:32:49.584863Z")
    private final Instant createdAt;

    @Schema(description = "판매량", example = "300")
    private final long salesVolume;

    public ItemSummary(final long id, final String categoryName, final String name, final int price, final String imageUrl, final boolean isInWishList, final Instant createdAt, final long salesVolume, final Long ratingSum, final Long ratingCount) {
        this.id = id;
        this.categoryName = categoryName;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isInWishList = isInWishList;
        this.createdAt = createdAt;
        this.salesVolume = salesVolume;
        if (ratingCount == 0) {
            this.rating = 0;
        } else {
            this.rating = (double) ratingSum / ratingCount;
        }
    }
}
