package com.ttarum.member.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.member.dto.request.CartAdditionRequest;
import com.ttarum.member.dto.request.NormalMemberRegister;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Member", description = "회원")
public interface MemberController {
    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패")
    })
    @PostMapping(consumes = "application/json")
    void registerNormalMember(@RequestBody NormalMemberRegister dto);

    /**
     * 제품 찜 메서드
     *
     * @param user   사용자
     * @param itemId 찜 목록에 추가할 제품의 Id 값
     * @return 빈 응답
     */
    @Operation(summary = "제품 찜하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @Parameter(name = "itemId", required = true, description = "제품의 Id 값", example = "1")
    @PostMapping
    ResponseEntity<Void> wishItem(@AuthenticationPrincipal CustomUserDetails user, @RequestParam long itemId);

    @Operation(summary = "장바구니에 제품 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @Parameters(value = {
            @Parameter(name = "itemId", description = "제품의 Id 값", example = "1", required = true),
            @Parameter(name = "amount", description = "장바구니에 담을 제품의 수량", example = "1", required = true)
    })
    @PostMapping
    ResponseEntity<Void> addToCart(@AuthenticationPrincipal CustomUserDetails user, CartAdditionRequest cartAdditionRequest);
}
