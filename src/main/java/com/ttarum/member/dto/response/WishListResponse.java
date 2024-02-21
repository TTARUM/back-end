package com.ttarum.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WishListResponse {

    private final List<ItemSummaryResponseForWishList> wishList;
}
