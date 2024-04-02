package com.ttarum.item.dto.response;

import com.ttarum.item.domain.redis.PopularItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "인기 검색어 응답 값")
public class PopularItemResponse {

    @Schema(description = "인기 검색어 목록")
    private final List<PopularItem> itemList;
}
