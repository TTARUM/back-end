package com.ttarum.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(name = "CartAdditionRequest", description = "장바구니에 넣을 제품과 수량이 담긴 DTO")
public class CartAdditionRequest {

    @Schema(description = "제품의 Id 값", example = "1")
    private final long itemId;

    @Schema(description = "장바구니에 담을 제품의 수량", example = "1")
    private final int amount;
}
