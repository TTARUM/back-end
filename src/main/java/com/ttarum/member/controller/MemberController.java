package com.ttarum.member.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.member.dto.request.AddressUpsertRequest;
import com.ttarum.member.dto.request.CartAdditionRequest;
import com.ttarum.member.dto.request.CartUpdateRequest;
import com.ttarum.member.dto.request.NormalMemberRegister;
import com.ttarum.member.dto.response.CartResponse;
import com.ttarum.member.dto.response.WishListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Member", description = "회원")
public interface MemberController {

    /**
     * 회원가입 메서드
     *
     * @param dto 회원가입에 필요한 데이터가 담긴 객체
     */
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
     * @param user   로그인한 회원
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

    /**
     * 찜 목록 조회 메서드
     *
     * @param user 로그인한 회원
     * @param page 페이지
     * @param size 페이지당 찜한 제품 개수
     * @return 찜 목록 리스트
     */
    @Operation(summary = "찜 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @Parameters(value = {
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "1"),
            @Parameter(name = "size", description = "페이지 당 찜한 제품 개수 (기본 값 8)", example = "8")
    })
    @GetMapping
    ResponseEntity<WishListResponse> getWishList(@AuthenticationPrincipal CustomUserDetails user,
                                                 Optional<Integer> page,
                                                 Optional<Integer> size);

    /**
     * 장바구니에 제품 추가
     *
     * @param user                로그인한 회원
     * @param cartAdditionRequest 추가될 제품과 수량이 담긴 객체
     * @return 빈 응답
     */
    @Operation(summary = "장바구니에 제품 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @Parameters(value = {
            @Parameter(name = "itemId", description = "제품의 Id 값", example = "1", required = true),
            @Parameter(name = "amount", description = "장바구니에 담을 제품의 수량", example = "1", required = true),
            @Parameter(name = "cartAdditionRequest", hidden = true)
    })
    @PostMapping
    ResponseEntity<Void> addToCart(@AuthenticationPrincipal CustomUserDetails user, CartAdditionRequest cartAdditionRequest);

    /**
     * 장바구니 조회 메서드
     *
     * @param user 로그인한 회원
     * @return 장바구니에 담긴 제품 목록
     */
    @Operation(summary = "장바구니 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping
    ResponseEntity<List<CartResponse>> getCartList(@AuthenticationPrincipal CustomUserDetails user);

    /**
     * 배송지 추가
     *
     * @param user                      로그인한 사용자
     * @param addressUpsertRequest      추가될 주소 정보
     * @return 빈 응답
     */
    @Operation(summary = "배송지 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping
    ResponseEntity<Void> addAddress(@AuthenticationPrincipal CustomUserDetails user, AddressUpsertRequest addressUpsertRequest);

    /**
     * 배송지 수정
     *
     * @param user                      로그인한 사용자
     * @param addressUpsertRequest      추가될 주소 정보
     * @return 빈 응답
     */
    @Operation(summary = "배송지 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping
    ResponseEntity<Void> updateAddress(@AuthenticationPrincipal CustomUserDetails user, @PathVariable final Long addressId, AddressUpsertRequest addressUpsertRequest);

    @PutMapping
    ResponseEntity<Void> updateItemAmountInCart(@AuthenticationPrincipal CustomUserDetails user, CartUpdateRequest cartUpdateRequest);
}
