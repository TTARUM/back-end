package com.ttarum.order.service;

import com.ttarum.order.domain.Order;
import com.ttarum.order.dto.response.OrderDetailResponse;
import com.ttarum.order.dto.response.summary.OrderItemSummary;
import com.ttarum.order.dto.response.summary.OrderSummary;
import com.ttarum.order.dto.response.summary.OrderSummaryListResponse;
import com.ttarum.order.exception.OrderForbiddenException;
import com.ttarum.order.exception.OrderNotFoundException;
import com.ttarum.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    private static final int DEFAULT_NUMBER_OF_ITEMS_PER_SUMMARY = 2;

    /**
     * 주문 내역 목록 조회 메서드
     *
     * @param memberId 회원의 Id 값
     * @param pageable 페이지네이션 객체
     * @return 주문 내역 목록
     */
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

    public OrderDetailResponse getOrderDetail(final long memberId, final long orderId) {
        Order order = getOrderById(orderId);
        if (!Objects.equals(order.getMember().getId(), memberId)) {
            throw new OrderForbiddenException();
        }
        List<OrderItemSummary> orderItemSummaryList = orderRepository.findOrderItemListByOrderId(orderId);
        return OrderDetailResponse.of(orderItemSummaryList, order);
    }

    private Order getOrderById(final long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }
}
