package com.ttarum.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderListResponse {

    private final List<OrderResponse> orderSummaryList;
}
