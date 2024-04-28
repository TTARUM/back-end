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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemControllerImpl implements ItemController {

    private static final int ITEM_DEFAULT_SIZE_PER_PAGE = 9;
    private static final int ITEM_SIMILAR_PRICE_DEFAULT_SIZE_PER_PAGE = 7;
    private static final int ITEM_POPULAR_IN_CATEGORY_DEFAULT_SIZE_PER_PAGE = 7;
    private final ItemService itemService;
    private final RedisService redisService;

    @Override
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailResponse> getDetail(@PathVariable final long itemId,
                                                        @RequestParam(required = false, defaultValue = "false") final boolean useSearch) {
        ItemDetailResponse response = itemService.getItemDetail(itemId);
        if (useSearch) {
            redisService.incrementSearchKeywordCount(response.getName(), itemId);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/list")
    public ResponseEntity<ItemSummaryResponse> getSummary(
            @RequestParam(required = false) final String query,
            @VerificationUser final Optional<LoggedInUser> user,
            @RequestParam final Optional<Integer> page,
            @RequestParam final Optional<Integer> size
    ) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(ITEM_DEFAULT_SIZE_PER_PAGE));
        ItemSummaryResponse response;
        if (user.isPresent()) {
            response = itemService.getItemSummaryList(query, pageRequest, user.get().getId());
        } else {
            response = itemService.getItemSummaryList(query, pageRequest);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/popular-list")
    public ResponseEntity<PopularItemResponse> getPopularItemList(@RequestParam(required = false, defaultValue = "5") final int number) {
        List<PopularItem> popularSearchKeywords = redisService.getPopularSearchKeywords(number);
        return ResponseEntity.ok(new PopularItemResponse(popularSearchKeywords));
    }

    @Override
    @GetMapping("/similar-price")
    public ResponseEntity<ItemSimilarPriceResponse> getSummaryWithSimilarPriceRange(@VerificationUser final Optional<LoggedInUser> user, final int price, final Optional<Integer> page, final Optional<Integer> size) {

        ItemSimilarPriceResponse response;
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(ITEM_SIMILAR_PRICE_DEFAULT_SIZE_PER_PAGE));
        if (user.isPresent()) {
            response = itemService.getItemSummaryListWithSimilarPriceRange(user.get().getId(), price, pageRequest);
        } else {
            response = itemService.getItemSummaryListWithSimilarPriceRange(price, pageRequest);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/popular-in-category")
    public ResponseEntity<PopularItemInCategoryResponse> getPopularItemSummaryListInCategory(@VerificationUser final Optional<LoggedInUser> user, final String category, final Optional<Integer> page, final Optional<Integer> size) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(ITEM_POPULAR_IN_CATEGORY_DEFAULT_SIZE_PER_PAGE));
        PopularItemInCategoryResponse response;
        if (user.isPresent()) {
            response = itemService.getPopularItemSummaryListInCategory(user.get().getId(), category, pageRequest);
        } else {
            response = itemService.getPopularItemSummaryListInCategory(category, pageRequest);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/category")
    public ResponseEntity<ItemSummaryResponse> getSummaryByCategory(@VerificationUser final Optional<LoggedInUser> user, @PathVariable final String category, final Optional<Integer> page, final Optional<Integer> size) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(ITEM_DEFAULT_SIZE_PER_PAGE));
        ItemSummaryResponse response;
        if (user.isPresent()) {
            //TODO: Update here to use userId
            response = itemService.getItemSummaryListByCategory(category, pageRequest);
        } else {
            response = itemService.getItemSummaryListByCategory(category, pageRequest);
        }
        return ResponseEntity.ok(response);
    }
}