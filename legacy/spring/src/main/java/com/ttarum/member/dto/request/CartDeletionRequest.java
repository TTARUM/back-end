package com.ttarum.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "장바구니 제품 제거 DTO")
public class CartDeletionRequest {

    @Schema(description = "제거될 제품의 Id 값이 담긴 리스트")
    private List<Long> itemIdList;
}
