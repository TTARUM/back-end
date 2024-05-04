package com.ttarum.order.dto.response;

import com.ttarum.order.dto.response.summary.OrderSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderListResponse {

    private final List<OrderSummary> orderSummaryList;
}
