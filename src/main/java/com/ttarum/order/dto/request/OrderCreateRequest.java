package com.ttarum.order.dto.request;

import com.ttarum.order.domain.Order;
import com.ttarum.order.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(name = "OrderCreateRequest", description = "주문 생성 요청 DTO")
public class OrderCreateRequest {
    @Schema(description = "주문 요청 사항", example = "부재 시 경비실에 맡겨주세요")
    private String comment;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;
    @Schema(description = "배송지 주소", example = "서울시 강남구 역삼동 123-456")
    private String address;
    @Schema(description = "수령인", example = "홍길동")
    private String recipient;
    @Schema(description = "주문 상품 목록")
    private List<OrderItem> orderItems;

    public Order toOrderEntity() {
        return Order.builder()
                .status(OrderStatus.COMPLETE)
                .comment(comment)
                .phoneNumber(phoneNumber)
                .address(address)
                .recipient(recipient)
                .build();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("comment:").append(comment).append("\n");
        sb.append("phoneNumber:").append(phoneNumber).append("\n");
        sb.append("address:").append(address).append("\n");
        sb.append("recipient:").append(recipient).append("\n");
        sb.append("orderItems:\n");
        for (OrderItem orderItem : orderItems) {
            sb.append(orderItem.getItemId()).append(" - ").append(orderItem.getQuantity()).append("\n");
        }
        return sb.toString();
    }
}
