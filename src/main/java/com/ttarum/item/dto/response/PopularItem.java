package com.ttarum.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "인기 상품")
public class PopularItem {

    @Schema(description = "제품 Id 값", example = "3")
    private long itemId;
    @Schema(description = "제품 이름", example = "베반 셀러스 온토게니")
    private String itemName;
    @Schema(description = "판매 횟수", example = "300")
    private long count;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopularItem that = (PopularItem) o;
        return itemId == that.itemId && Objects.equals(itemName, that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, itemId);
    }
}
