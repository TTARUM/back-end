package com.ttarum.order.service;

import com.ttarum.member.domain.Member;
import com.ttarum.member.repository.MemberRepository;
import com.ttarum.order.domain.Order;
import com.ttarum.order.dto.response.OrderDetailResponse;
import com.ttarum.order.dto.response.summary.OrderItemSummary;
import com.ttarum.order.dto.response.summary.OrderSummary;
import com.ttarum.order.dto.response.summary.OrderSummaryListResponse;
import com.ttarum.order.exception.OrderForbiddenException;
import com.ttarum.order.exception.OrderNotFoundException;
import com.ttarum.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.ttarum.order.domain.OrderStatus.*;
import static com.ttarum.order.domain.PaymentMethod.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    MemberRepository memberRepository;
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

    @Test
    @DisplayName("주문 조회")
    void getOrderDetail() {
        // given
        long orderId = 1;
        long memberId = 1;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Order order = Order.builder()
                .id(orderId)
                .status(SHIPPING)
                .comment("배송 메모")
                .phoneNumber("010-0000-0000")
                .address("중구 황학동")
                .deliveryFee(2500)
                .recipient("유지민")
                .price(46_000L)
                .paymentMethod(CREDIT_CARD)
                .member(member)
                .build();
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

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.findOrderItemListByOrderId(orderId)).thenReturn(orderItemSummaryList);

        // when
        OrderDetailResponse response = orderService.getOrderDetail(memberId, orderId);

        // then
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).findOrderItemListByOrderId(orderId);
        assertThat(response.getOrderItemSummaryList()).isEqualTo(orderItemSummaryList);
        assertThat(response.getOrderStatus()).isEqualTo(SHIPPING);
        assertThat(response.getComment()).isEqualTo("배송 메모");
        assertThat(response.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(response.getAddress()).isEqualTo("중구 황학동");
        assertThat(response.getDeliveryFee()).isEqualTo(2500);
        assertThat(response.getRecipient()).isEqualTo("유지민");
        assertThat(response.getPrice()).isEqualTo(46_000L);
        assertThat(response.getPaymentMethod()).isEqualTo(CREDIT_CARD);
    }

    @Test
    @DisplayName("주문 조회 - 주문이 존재하지 않는 경우 예외 발생")
    void getOrderDetailFailedByInvalidOrderId() {
        // given
        long orderId = 1;
        long memberId = 1;
        Member member = Member.builder()
                .id(memberId)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getOrderDetail(memberId, orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("주문 조회 - 다른 회원의 주문을 조회하는 경우 예외 발생")
    void getOrderDetailFailedByForbidden() {
        // given
        long orderId = 1;
        long memberId = 1;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Order order = Order.builder()
                .id(orderId)
                .status(SHIPPING)
                .comment("배송 메모")
                .phoneNumber("010-0000-0000")
                .address("중구 황학동")
                .deliveryFee(2500)
                .recipient("유지민")
                .price(46_000L)
                .paymentMethod(CREDIT_CARD)
                .member(Member.builder().id(2L).build())
                .build();


        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.getOrderDetail(memberId, orderId))
                .isInstanceOf(OrderForbiddenException.class);
    }
}