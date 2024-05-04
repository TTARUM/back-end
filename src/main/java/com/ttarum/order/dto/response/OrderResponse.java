package com.ttarum.order.dto.response;

import com.ttarum.order.domain.Order;
import com.ttarum.order.domain.OrderStatus;
import com.ttarum.order.domain.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;


@Builder
@Getter
public class OrderResponse {
    private final long orderId;
    private final Instant dateTime;
    private final OrderStatus orderStatus;
    private final long price;
    private final int deliveryFee;
    private final String recipient;
    private final String phoneNumber;
    private final String address;
    private final String comment;
    private final PaymentMethod paymentMethod;

    public static OrderResponse fromEntity(Order entity) {
        return OrderResponse.builder()
                .orderId(entity.getId())
                .dateTime(entity.getCreatedAt())
                .orderStatus(entity.getStatus())
                .price(entity.getPrice())
                .deliveryFee(entity.getDeliveryFee())
                .recipient(entity.getRecipient())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .comment(entity.getComment())
                .paymentMethod(entity.getPaymentMethod())
                .build();
    }
}
