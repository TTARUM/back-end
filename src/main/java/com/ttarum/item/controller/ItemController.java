package com.ttarum.item.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.LoggedInUser;
import com.ttarum.item.dto.response.ItemDetailResponse;
import com.ttarum.item.dto.response.ItemSimilarPriceResponse;
import com.ttarum.item.dto.response.summary.ItemSummaryResponse;
import com.ttarum.item.dto.response.*;
import com.ttarum.item.domain.redis.PopularItem;
import com.ttarum.item.service.ItemService;
import com.ttarum.item.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
@Tag(name = "item", description = "제품")
public class ItemController {

    private static final int ITEM_DEFAULT_SIZE_PER_PAGE = 9;
    private static final int ITEM_SIMILAR_PRICE_DEFAULT_SIZE_PER_PAGE = 7;
    private static final int ITEM_POPULAR_IN_CATEGORY_DEFAULT_SIZE_PER_PAGE = 7;
    private final ItemService itemService;
    private final RedisService redisService;

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
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailResponse> getDetail(
            @PathVariable final long itemId,
            @RequestParam(required = false, defaultValue = "false") final boolean useSearch
    ) {
        ItemDetailResponse response = itemService.getItemDetail(itemId);
        if (useSearch) {
            redisService.incrementSearchKeywordCount(response.getName(), itemId);
        }
        return ResponseEntity.ok(response);
    }

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
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "1"),
            @Parameter(name = "size", description = "한 페이지당 제품 수 (기본 값 9개)", example = "9")
    })
    @GetMapping("/list")
    public ResponseEntity<ItemSummaryResponse> getSummary(
            @RequestParam(required = false) final String query,
            @RequestParam final Optional<Integer> page,
            @RequestParam final Optional<Integer> size,
            @Parameter(hidden = true) @VerificationUser final Optional<LoggedInUser> user
    ) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(ITEM_DEFAULT_SIZE_PER_PAGE));
        ItemSummaryResponse response;
        if (user.isPresent()) {
            //TODO: Update here to use userId
            response = itemService.getItemSummaryList(query, pageRequest);
        } else {
            response = itemService.getItemSummaryList(query, pageRequest);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 인기 검색어 조회
     *
     * @param number 조회할 인기 검색어 개수
     * @return 인기 검색어 목록
     */
    @Operation(summary = "인기 검색어 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @Parameter(name = "number", description = "조회할 인기 검색어 개수 (기본 값 5)", example = "5")
    @GetMapping("/popular-list")
    public ResponseEntity<PopularItemResponse> getPopularItemList(
            @RequestParam(required = false, defaultValue = "5") final int number
    ) {
        List<PopularItem> popularSearchKeywords = redisService.getPopularSearchKeywords(number);
        return ResponseEntity.ok(new PopularItemResponse(popularSearchKeywords));
    }

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
    @GetMapping("/similar-price")
    public ResponseEntity<ItemSimilarPriceResponse> getSummaryWithSimilarPriceRange(
            final int price, final Optional<Integer> page,
            final Optional<Integer> size,
            @Parameter(hidden = true) @VerificationUser final Optional<LoggedInUser> user
    ) {

        ItemSimilarPriceResponse response;
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(ITEM_SIMILAR_PRICE_DEFAULT_SIZE_PER_PAGE));
        if (user.isPresent()) {
            response = itemService.getItemSummaryListWithSimilarPriceRange(user.get().getId(), price, pageRequest);
        } else {
            response = itemService.getItemSummaryListWithSimilarPriceRange(price, pageRequest);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 카테고리 인기상품 조회
     *
     * @param user     로그인한 회원
     * @param categoryId 카테고리 ID
     * @param page     페이지 넘버
     * @param size     페이지당 조회할 제품의 수
     * @return 조회된 인기상품
     */
    @Operation(summary = "카테고리 인기상품 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @Parameters(value = {
            @Parameter(name = "category", description = "카테고리 ID", example = "1"),
            @Parameter(name = "page", description = "페이지 넘버 (기본값 0)", example = "1"),
            @Parameter(name = "size", description = "한 페이지당 제품 수 (기본 값 7)", example = "7")
    })
    @GetMapping("/popular-in-category/{categoryId}")
    public ResponseEntity<PopularItemInCategoryResponse> getPopularItemSummaryListInCategory(
            @PathVariable final Long categoryId,
            final Optional<Integer> page,
            final Optional<Integer> size,
            @Parameter(hidden = true) @VerificationUser final Optional<LoggedInUser> user
    ) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(ITEM_POPULAR_IN_CATEGORY_DEFAULT_SIZE_PER_PAGE));
        PopularItemInCategoryResponse response;
        if (user.isPresent()) {
            response = itemService.getPopularItemSummaryListInCategory(user.get().getId(), categoryId, pageRequest);
        } else {
            response = itemService.getPopularItemSummaryListInCategory(categoryId, pageRequest);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 카테고리별 제품 조회
     *
     * @param user     로그인한 회원
     * @param categoryId 카테고리 ID
     * @param page     페이지 넘버
     * @param size     페이지당 조회할 제품의 수
     * @return 조회된 제품 리스트
     */
    @Operation(summary = "카테고리별 제품 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @Parameters(value = {
            @Parameter(name = "page", description = "페이지 넘버 (기본값 0)", example = "0"),
            @Parameter(name = "size", description = "한 페이지당 제품 수 (기본 값 9)", example = "9")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ItemSummaryResponse> getSummaryByCategory(
            @PathVariable final Long categoryId,
            final Optional<Integer> page,
            final Optional<Integer> size,
            @Parameter(hidden = true) @VerificationUser final Optional<LoggedInUser> user
    ) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(ITEM_DEFAULT_SIZE_PER_PAGE));
        ItemSummaryResponse response;
        if (user.isPresent()) {
            response = itemService.getItemSummaryListByCategory(user.get().getId(), categoryId, pageRequest);
        } else {
            response = itemService.getItemSummaryListByCategory(categoryId, pageRequest);
        }
        return ResponseEntity.ok(response);
    }
}