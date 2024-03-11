package com.ttarum.order.dto.response;

import com.ttarum.order.domain.Order;
import com.ttarum.order.domain.OrderStatus;
import com.ttarum.order.domain.PaymentMethod;
import com.ttarum.order.dto.response.summary.OrderItemSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderDetailResponse {

    private final List<OrderItemSummary> orderItemSummaryList;
    private final OrderStatus orderStatus;
    private final Instant orderDate;
    private final long price;
    private final String recipient;
    private final String phoneNumber;
    private final String address;
    private final String comment;
    private final PaymentMethod paymentMethod;
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
