package com.ttarum.item.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ItemSummaryResponse {

    private final List<ItemSummary> itemSummaryResponseList;

    public ItemSummaryResponse(final List<ItemSummary> itemSummaryResponseList) {
        this.itemSummaryResponseList = itemSummaryResponseList;
    }
}
