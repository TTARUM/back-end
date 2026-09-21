package com.ttarum.order.service;

import com.ttarum.member.domain.Member;
import com.ttarum.member.repository.MemberRepository;
import com.ttarum.order.domain.Order;
import com.ttarum.order.exception.OrderForbiddenException;
import com.ttarum.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ttarum.order.domain.OrderStatus.SHIPPING;
import static com.ttarum.order.domain.PaymentMethod.CREDIT_CARD;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    OrderService orderService;

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