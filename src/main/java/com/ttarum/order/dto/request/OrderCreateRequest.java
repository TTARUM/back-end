package com.ttarum.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ttarum.member.domain.Member;
import com.ttarum.order.domain.Order;
import com.ttarum.order.domain.OrderStatus;
import com.ttarum.order.domain.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(name = "OrderCreateRequest", description = "주문 생성 요청 DTO")
public class OrderCreateRequest {
    private static final int DEFAULT_DELIVERY_FEE = 3000;

    // TODO: 주문 요청사항 필수 값인지 확인하고 필요시 스키마 변경하기
    @Schema(description = "주문 요청 사항", example = "부재 시 경비실에 맡겨주세요")
    private final String comment;

    @NotBlank(message = "전화번호는 필수 값입니다.")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private final String phoneNumber;

    @NotBlank(message = "배송지 주소는 필수 값입니다.")
    @Schema(description = "배송지 주소", example = "서울시 강남구 역삼동 123-456")
    private final String address;

    @NotBlank(message = "수령인은 필수 값입니다.")
    @Schema(description = "수령인", example = "홍길동")
    private final String recipient;

    @Size(min = 1, message = "주문 상품은 최소 1개 이상이어야 합니다.")
    @Schema(description = "주문 상품 목록")
    @JsonProperty("orderItems")
    private final List<OrderItemRequest> orderItemRequests;

    @Schema(description = "쿠폰 ID", example = "1")
    private Long couponId;

    @NotNull(message = "총 주문 금액은 필수 값입니다.")
    @Positive(message = "총 주문 금액은 0보다 커야 합니다.")
    @Schema(description = "총 주문 금액", example = "30000")
    private final Long totalPrice;

    public Order toOrderEntity(Member member, long discountPrice) {
        return Order.builder()
                .status(OrderStatus.COMPLETE)
                .comment(comment)
                .phoneNumber(phoneNumber)
                .address(address)
                .deliveryFee(DEFAULT_DELIVERY_FEE)
                .recipient(recipient)
                .price(totalPrice)
                .discountPrice(discountPrice)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .member(member)
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
        for (OrderItemRequest orderItemRequest : orderItemRequests) {
            sb.append(orderItemRequest.getItemId()).append("\t - ").append(orderItemRequest.getQuantity()).append("\n");
        }
        sb.append("couponId:").append(couponId).append("\n");
        sb.append("totalPrice:").append(totalPrice).append("\n");
        return sb.toString();
    }
}
