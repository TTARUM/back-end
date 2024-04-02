package com.ttarum.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "찜 목록 DTO")
public class WishlistResponse {

    @Schema(description = "찜 리스트")
    private final List<ItemSummaryResponseForWishlist> wishlist;
}
