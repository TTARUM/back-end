package com.ttarum.order.service;

import com.ttarum.order.domain.Order;
import com.ttarum.order.dto.response.summary.OrderItemSummary;
import com.ttarum.order.dto.response.summary.OrderSummary;
import com.ttarum.order.dto.response.summary.OrderSummaryListResponse;
import com.ttarum.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @InjectMocks
    OrderService orderService;

    @Test
    @DisplayName("주문 내역 목록 조회")
    void getOrderSummaryList() {
        // given
        long memberId = 1;
        long orderId = 1;
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Order> orderList = List.of(
                Order.builder()
                        .id(orderId)
                        .build());
        List<OrderItemSummary> orderItemSummaryList = List.of(
                OrderItemSummary.builder()
                        .itemId(1)
                        .itemImageUrl("ttarum.image.url")
                        .itemName("item")
                        .itemPrice(3000)
                        .amount(3)
                        .hasReview(false)
                        .build()
        );

        when(orderRepository.findOrderListByMemberId(memberId, pageRequest)).thenReturn(orderList);
        when(orderRepository.findOrderItemListByOrderId(orderId, 2)).thenReturn(orderItemSummaryList);

        // when
        OrderSummaryListResponse response = orderService.getOrderSummaryList(memberId, pageRequest);

        List<OrderSummary> orderSummaryList = response.getOrderSummaryList();

        // then
        verify(orderRepository, times(1)).findOrderListByMemberId(memberId, pageRequest);
        verify(orderRepository, times(1)).findOrderItemListByOrderId(orderId, 2);
        assertThat(orderSummaryList).size().isEqualTo(1);
        assertThat(orderSummaryList.get(0).getOrderId()).isEqualTo(orderId);
        assertThat(orderSummaryList.get(0).getOrderItemSummaryList()).size().isEqualTo(1);
        assertThat(orderSummaryList.get(0).getOrderItemSummaryList().get(0).getItemId()).isEqualTo(1);
        assertThat(orderSummaryList.get(0).getOrderItemSummaryList().get(0).getItemImageUrl()).isEqualTo("ttarum.image.url");
        assertThat(orderSummaryList.get(0).getOrderItemSummaryList().get(0).getItemName()).isEqualTo("item");
        assertThat(orderSummaryList.get(0).getOrderItemSummaryList().get(0).getItemPrice()).isEqualTo(3000);
        assertThat(orderSummaryList.get(0).getOrderItemSummaryList().get(0).getAmount()).isEqualTo(3);
        assertThat(orderSummaryList.get(0).getOrderItemSummaryList().get(0).isHasReview()).isFalse();
    }
}