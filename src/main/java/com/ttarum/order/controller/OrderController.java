package com.ttarum.order.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.order.dto.response.summary.OrderSummaryListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "order", description = "주문")
public interface OrderController {

    /**
     * 주문 내역 목록 조회
     *
     * @param user 로그인한 회원
     * @param page 페이지 넘버
     * @param size 페이지당 주문 내역 개수
     * @return 주문 내역 목록
     */
    @Operation(summary = "주문 내역 목록 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @Parameters(value = {
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "0"),
            @Parameter(name = "size", description = "페이지당 주문 내역 개수 (기본 값 5)", example = "5")
    })
    @GetMapping
    ResponseEntity<OrderSummaryListResponse> getOrderList(@AuthenticationPrincipal CustomUserDetails user,
                                                          @RequestParam(required = false, defaultValue = "0") int page,
                                                          @RequestParam(required = false, defaultValue = "5") int size);
}
