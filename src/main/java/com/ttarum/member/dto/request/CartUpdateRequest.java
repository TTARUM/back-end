package com.ttarum.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니에 있는 제품 업데이트 DTO")
public class CartUpdateRequest {

    @Schema(description = "수정할 수량", example = "365")
    private int amount;
}
