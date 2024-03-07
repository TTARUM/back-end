package com.ttarum.order.service;

import com.ttarum.order.domain.Order;
import com.ttarum.order.dto.response.summary.OrderItemSummary;
import com.ttarum.order.dto.response.summary.OrderSummary;
import com.ttarum.order.dto.response.summary.OrderSummaryListResponse;
import com.ttarum.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    private static final int DEFAULT_NUMBER_OF_ITEMS_PER_SUMMARY = 2;

    public OrderSummaryListResponse getOrderSummaryList(final long memberId, final Pageable pageable) {
        List<Order> orderList = orderRepository.findOrderListByMemberId(memberId, pageable);
        List<OrderSummary> orderSummaryList = new ArrayList<>();

        for (Order order : orderList) {
            List<OrderItemSummary> orderItemSummaryList = orderRepository.findOrderItemListByOrderId(order.getId(), DEFAULT_NUMBER_OF_ITEMS_PER_SUMMARY);
            orderSummaryList.add(OrderSummary.builder()
                    .orderId(order.getId())
                    .dateTime(order.getCreatedAt())
                    .orderItemSummaryList(orderItemSummaryList)
                    .build()
            );
        }
        return new OrderSummaryListResponse(orderSummaryList);
    }
}
