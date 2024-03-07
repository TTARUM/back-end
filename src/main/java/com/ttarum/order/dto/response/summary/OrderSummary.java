package com.ttarum.order.dto.response.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderSummary {

    private final long orderId;
    private final Instant dateTime;
    private final List<OrderItemSummary> orderItemSummaryList;
}
