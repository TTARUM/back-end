package com.ttarum.item.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.item.dto.response.ItemDetailResponse;
import com.ttarum.item.dto.response.ItemSimilarPriceResponse;
import com.ttarum.item.dto.response.summary.ItemSummaryResponse;
import com.ttarum.item.dto.response.PopularItemResponse;
import com.ttarum.item.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

@Tag(name = "item", description = "제품")
public interface ItemController {

    /**
     * 특정 제품의 상세정보 조회 메서드
     *
     * @param itemId 제품의 Id 값
     * @return 제품의 상세정보
     */
    @Operation(summary = "특정 제품의 상세정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 제품")
    })
    @Parameters(value = {
            @Parameter(name = "itemId", description = "제품의 아이디", example = "1", required = true),
            @Parameter(name = "useSearch", description = "검색을 이용해 제품을 조회하는지 여부 (없을 시 false 적용)", example = "true")
    })
    @GetMapping
    ResponseEntity<ItemDetailResponse> getDetail(@PathVariable long itemId, @RequestParam(required = false, defaultValue = "false") boolean useSearch);

    /**
     * 요약된 제품 정보에 대한 검색 메서드
     *
     * @param query 검색어
     * @param user  로그인한 사용자 여부를 확인하기 위한 객체
     * @param page  페이지 넘버
     * @param size  아이템 사이즈
     * @return 검색된 제품 목록
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Operation(summary = "이름으로 제품 검색")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "성공")
    @Parameters(value = {
            @Parameter(name = "query", description = "검색어"),
            @Parameter(name = "user", hidden = true),
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "1"),
            @Parameter(name = "size", description = "한 페이지당 제품 수 (기본 값 9개)", example = "9")
    })
    @GetMapping
    ResponseEntity<ItemSummaryResponse> getSummary(@RequestParam(required = false) final String query,
                                                   @VerificationUser final Optional<User> user,
                                                   final Optional<Integer> page,
                                                   final Optional<Integer> size);

    /**
     * 인기 검색어 조회
     *
     * @param number 조회할 인기 검색어 개수
     * @return 인기 검색어 목록
     */
    @Operation(summary = "인기 검색어 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @Parameter(name = "number", description = "조회할 인기 검색어 개수 (기본 값 5)", example = "5")
    @GetMapping
    ResponseEntity<PopularItemResponse> getPopularItemList(@RequestParam(required = false, defaultValue = "5") int number);

    /**
     * 가격대가 비슷한 술 조회
     *
     * @param user  로그인한 사용자 여부를 확인하기 위한 객체
     * @param price 가격대
     * @return 조회된 제품 리스트
     */
    @Operation(summary = "가격대가 비슷한 술 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @Parameters(value = {
            @Parameter(name = "price", description = "가격", example = "17000"),
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "1"),
            @Parameter(name = "size", description = "페이지당 개수 (기본 값 7)", example = "7")
    })
    @GetMapping
    ResponseEntity<ItemSimilarPriceResponse> getSummaryWithSimilarPriceRange(@VerificationUser Optional<User> user, int price, Optional<Integer> page, Optional<Integer> size);

    /**
     * 카테고리 인기상품 조회
     *
     * @param user     로그인한 회원
     * @param category 카테고리 이름
     * @param page     페이지 넘버
     * @param size     페이지당 조회할 제품의 수
     * @return 조회된 인기상품
     */
    @Operation(summary = "카테고리 인기상품 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @Parameters(value = {
            @Parameter(name = "category", description = "카테고리 이름", example = "red"),
            @Parameter(name = "page", description = "페이지 넘버 (기본값 0)", example = "1"),
            @Parameter(name = "size", description = "한 페이지당 제품 수 (기본 값 7)", example = "7")
    })
    @GetMapping
    ResponseEntity<PopularItemInCategoryResponse> getPopularItemSummaryListInCategory(@VerificationUser Optional<User> user, String category, Optional<Integer> page, Optional<Integer> size);
}
