package com.ttarum.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PopularItemInCategoryResponse {

    private final List<PopularItemSummaryInCategory> itemSummaryList;
}
