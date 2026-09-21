package com.ttarum.order.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.order.dto.request.OrderCreateRequest;
import com.ttarum.order.dto.response.OrderDetailResponse;
import com.ttarum.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "order", description = "주문")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping()
    public ResponseEntity<Long> createOrder(
            @Validated @RequestBody final OrderCreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user
    ){
        Long ret = orderService.createOrder(request, user.getId());
        return ResponseEntity.ok(ret);
    }

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
    @GetMapping("/list")
    public ResponseEntity<List<OrderDetailResponse>> getOrderList(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<OrderDetailResponse> response = orderService.getOrderList(user.getId(), pageRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 주문 조회
     *
     * @param user    로그인한 회원
     * @param orderId 조회할 주문의 Id 값
     * @return 주문의 세부사항
     */
    @Operation(summary = "주문 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @Parameter(name = "orderId", description = "조회할 주문의 Id 값", example = "1")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @PathVariable final long orderId,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        OrderDetailResponse orderDetailResponse = orderService.getOrderDetail(user.getId(), orderId);
        return ResponseEntity.ok(orderDetailResponse);
    }
}
