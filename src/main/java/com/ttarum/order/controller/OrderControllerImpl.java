package com.ttarum.order.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.order.dto.response.OrderDetailResponse;
import com.ttarum.order.dto.response.summary.OrderSummaryListResponse;
import com.ttarum.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;

    @Override
    @GetMapping("/list")
    public ResponseEntity<OrderSummaryListResponse> getOrderList(@AuthenticationPrincipal final CustomUserDetails user,
                                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                                 @RequestParam(required = false, defaultValue = "5") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        OrderSummaryListResponse response = orderService.getOrderSummaryList(user.getId(), pageRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@AuthenticationPrincipal final CustomUserDetails user, @PathVariable final long orderId) {
        OrderDetailResponse orderDetailResponse = orderService.getOrderDetail(user.getId(), orderId);
        return ResponseEntity.ok(orderDetailResponse);
    }
}
