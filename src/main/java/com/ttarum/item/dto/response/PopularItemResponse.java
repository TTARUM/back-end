package com.ttarum.item.dto.response;

import com.ttarum.item.domain.redis.PopularItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PopularItemResponse {

    private final List<PopularItem> itemList;
}
