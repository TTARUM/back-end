package com.ttarum.order.dto.response;

import com.ttarum.order.domain.Order;
import com.ttarum.order.domain.OrderStatus;
import com.ttarum.order.domain.PaymentMethod;
import com.ttarum.order.dto.response.summary.OrderItemSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "주문 조회 DTO")
public class OrderDetailResponse {

    @Schema(description = "주문 내 제품 DTO 리스트")
    private final List<OrderItemSummary> orderItemSummaryList;

    @Schema(description = "주문 상태", example = "배송중")
    private final OrderStatus orderStatus;

    @Schema(description = "주문 일자")
    private final Instant orderDate;

    @Schema(description = "결제 금액", example = "30000")
    private final long price;

    @Schema(description = "수령인", example = "유지민")
    private final String recipient;

    @Schema(description = "휴대폰 번호", example = "010-0000-0000")
    private final String phoneNumber;

    @Schema(description = "주문", example = "서울 성동구 뚝섬로 273, 1001호 [04770]")
    private final String address;

    @Schema(description = "배송 메모", example = "문 앞에 놔주세요.")
    private final String comment;

    @Schema(description = "결제 수단", example = "신용카드")
    private final PaymentMethod paymentMethod;

    @Schema(description = "배송비", example = "2500")
    private final int deliveryFee;

    public static OrderDetailResponse of(final List<OrderItemSummary> orderItemSummaryList, final Order order) {
        return OrderDetailResponse.builder()
                .orderItemSummaryList(orderItemSummaryList)
                .orderStatus(order.getStatus())
                .orderDate(order.getCreatedAt())
                .price(order.getPrice())
                .recipient(order.getRecipient())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .comment(order.getComment())
                .paymentMethod(order.getPaymentMethod())
                .deliveryFee(order.getDeliveryFee())
                .build();
    }
}
