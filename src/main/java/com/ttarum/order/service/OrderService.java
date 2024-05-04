package com.ttarum.order.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Member;
import com.ttarum.member.exception.MemberNotFoundException;
import com.ttarum.member.repository.MemberRepository;
import com.ttarum.order.domain.Order;
import com.ttarum.order.dto.request.OrderCreateRequest;
import com.ttarum.order.dto.request.OrderItemRequest;
import com.ttarum.order.dto.response.OrderDetailResponse;
import com.ttarum.order.dto.response.summary.OrderItemSummary;
import com.ttarum.order.dto.response.summary.OrderSummary;
import com.ttarum.order.dto.response.summary.OrderSummaryListResponse;
import com.ttarum.order.exception.OrderException;
import com.ttarum.order.exception.OrderForbiddenException;
import com.ttarum.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    private static final int DEFAULT_NUMBER_OF_ITEMS_PER_SUMMARY = 2;

    public void createOrder(final OrderCreateRequest request, final long memberId) {
        Member member = getMemberById(memberId);

        List<Long> itemIds = request.getOrderItemRequests()
                .stream().map(OrderItemRequest::getItemId).toList();
        List<Item> items = itemRepository.findAllById(itemIds);

        if (!validateOrderItems(request.getOrderItemRequests(), items)) {
            throw OrderException.itemNotFound();
        }

        long totalPrice = calculateTotalPrice(request.getOrderItemRequests(), items);
        Order orderEntity = request.toOrderEntity(totalPrice, member);
        orderRepository.save(orderEntity);
    }

    private boolean validateOrderItems(List<OrderItemRequest> orderItemRequests, List<Item> items) {
        return orderItemRequests.size() == items.size();
    }

    private long calculateTotalPrice(List<OrderItemRequest> orderItemRequests, List<Item> items) {
        Map<Long, Integer> itemQuantity = orderItemRequests.stream()
                .collect(Collectors.toMap(OrderItemRequest::getItemId, OrderItemRequest::getQuantity));

        long ret = 0L;
        for (Item item : items) {
            ret += (long) item.getPrice() * itemQuantity.get(item.getId());
        }
        return ret;
    }

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

    /**
     * 주문 세부사항 조회 메서드
     *
     * @param memberId 회원의 Id 값
     * @param orderId  주문의 Id 값
     * @return 주문의 세부사항
     * @throws MemberNotFoundException 회원을 찾지 못하였을 경우 발생한다.
     * @throws OrderException  주문을 찾지 못하였을 경우 발생한다.
     * @throws OrderForbiddenException 회원의 주문이 아닌 경우 발생한다.
     */
    public OrderDetailResponse getOrderDetail(final long memberId, final long orderId) {
        Member member = getMemberById(memberId);
        Order order = getOrderById(orderId);
        if (!member.isMyOrder(order)) {
            throw new OrderForbiddenException();
        }
        List<OrderItemSummary> orderItemSummaryList = orderRepository.findOrderItemListByOrderId(orderId);
        return OrderDetailResponse.of(orderItemSummaryList, order);
    }

    private Order getOrderById(final long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderException::notFound);
    }

    private Member getMemberById(final long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}
