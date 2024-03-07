package com.ttarum.order.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.order.dto.response.summary.OrderSummaryListResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "order", description = "주문")
public interface OrderController {

    @GetMapping
    ResponseEntity<OrderSummaryListResponse> getOrderList(@AuthenticationPrincipal CustomUserDetails user,
                                                          @RequestParam(required = false, defaultValue = "0") int size,
                                                          @RequestParam(required = false, defaultValue = "5") int page);
}
