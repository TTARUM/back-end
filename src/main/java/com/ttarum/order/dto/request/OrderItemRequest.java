package com.ttarum.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "OrderItem", description = "주문 상품 정보")
public class OrderItemRequest {
    @Schema(description = "상품 ID", example = "1")
    private Long itemId;
    @Schema(description = "수량", example = "2")
    private Integer quantity;
}
