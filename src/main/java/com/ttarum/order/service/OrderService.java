package com.ttarum.order.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.coupon.Coupon;
import com.ttarum.member.exception.MemberNotFoundException;
import com.ttarum.member.repository.CouponRepository;
import com.ttarum.member.repository.MemberCouponRepository;
import com.ttarum.member.repository.MemberRepository;
import com.ttarum.order.domain.Order;
import com.ttarum.order.domain.OrderItem;
import com.ttarum.order.dto.request.OrderCreateRequest;
import com.ttarum.order.dto.request.OrderItemRequest;
import com.ttarum.order.dto.response.OrderDetailResponse;
import com.ttarum.order.dto.response.OrderItemSummary;
import com.ttarum.order.exception.OrderException;
import com.ttarum.order.exception.OrderForbiddenException;
import com.ttarum.order.repository.OrderItemRepository;
import com.ttarum.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;

    /**
     * 주문 생성 메서드
     *
     * @param request 주문 생성 요청
     * @param memberId 회원의 Id 값
     * @throws OrderException 주문 생성에 실패하였을 경우 발생한다.
     */
    public Long createOrder(final OrderCreateRequest request, final long memberId) {
        Member member = getMemberById(memberId);

        List<Long> itemIds = request.getOrderItemRequests()
                .stream().map(OrderItemRequest::getItemId).toList();
        List<Item> items = itemRepository.findAllById(itemIds);

        if (!validateOrderItems(request.getOrderItemRequests(), items)) {
            throw OrderException.itemNotFound();
        }

        Map<Long, Long> itemQuantity = itemQuantity(request.getOrderItemRequests());
        countUpItemOrderCount(items, itemQuantity);

        long totalPrice = calculateTotalPrice(itemQuantity, items);
        if (request.getCouponId() != null) {
            Coupon coupon = couponRepository.findById(request.getCouponId())
                    .orElseThrow(OrderException::couponNotFound);

            memberCouponRepository.findByMemberIdAndCouponId(memberId, request.getCouponId())
                    .orElseThrow(OrderException::couponNotFound);

            totalPrice = coupon.calculatePrice(totalPrice);
            memberCouponRepository.deleteMemberCouponByMemberIdAndCouponId(memberId, request.getCouponId());
        }

        if (totalPrice != request.getTotalPrice()) {
            throw OrderException.priceNotMatch();
        }

        Order orderEntity = request.toOrderEntity(member);
        Order saved = orderRepository.save(orderEntity);

        List<OrderItem> orderItems = orderItemsList(orderEntity, items, itemQuantity);
        orderItemRepository.saveAll(orderItems);
        return saved.getId();
    }

    private boolean validateOrderItems(List<OrderItemRequest> orderItemRequests, List<Item> items) {
        return orderItemRequests.size() == items.size();
    }

    private Map<Long, Long> itemQuantity(List<OrderItemRequest> orderItemRequests) {
        return orderItemRequests.stream()
                .collect(Collectors.toMap(OrderItemRequest::getItemId, OrderItemRequest::getQuantity));
    }

    private long calculateTotalPrice(Map<Long, Long> itemQuantity, List<Item> items) {
        long ret = 0L;
        for (Item item : items) {
            ret += (long) item.getPrice() * itemQuantity.get(item.getId());
        }
        return ret;
    }

    private void countUpItemOrderCount(List<Item> items, Map<Long, Long> itemQuantity) {
        for (Item item : items) {
            item.addOrderCount(itemQuantity.get(item.getId()));
        }
    }

    private List<OrderItem> orderItemsList(Order order, List<Item> items, Map<Long, Long> itemQuantity) {
        return items.stream().map(item -> new OrderItem(order, item, itemQuantity.get(item.getId()))
        ).toList();
    }

    /**
     * 주문 내역 목록 조회 메서드
     *
     * @param memberId 회원의 Id 값
     * @param pageable 페이지네이션 객체
     * @return 주문 내역 목록
     */
    public List<OrderDetailResponse> getOrderList(final long memberId, final Pageable pageable) {
        List<Order> orderList = orderRepository.findOrderListByMemberId(memberId, pageable);

        return orderList.stream()
                .map(o -> {
                    List<OrderItemSummary> orderItemSummaryList = orderItemRepository.findOrderItemListByOrderId(o.getId());
                    return OrderDetailResponse.of(orderItemSummaryList, o);
                })
                .toList();
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
        List<OrderItemSummary> orderItemSummaryList = orderItemRepository.findOrderItemListByOrderId(orderId);
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
