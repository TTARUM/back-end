package com.ttarum.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderSummaryListResponse {

    private final long orderId;
    private final Instant dateTime;
    private final List<OrderItemSummary> orderItemSummaryList;
}
