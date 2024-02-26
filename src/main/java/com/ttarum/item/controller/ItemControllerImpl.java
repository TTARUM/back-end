package com.ttarum.item.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.item.dto.response.ItemDetailResponse;
import com.ttarum.item.dto.response.ItemSummaryResponse;
import com.ttarum.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemControllerImpl implements ItemController {

    private static final int ITEM_DEFAULT_SIZE_PER_PAGE = 9;
    private final ItemService itemService;

    @Override
    @GetMapping
    public ResponseEntity<ItemDetailResponse> getDetail(final Long id) {
        return ResponseEntity.ok(itemService.getItemDetail(id));
    }

    @Override
    @GetMapping("/list")
    public ResponseEntity<ItemSummaryResponse> getSummary(
            @RequestParam(required = false) final String query,
            @VerificationUser final User user,
            @RequestParam final Optional<Integer> page,
            @RequestParam final Optional<Integer> size
    ) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(ITEM_DEFAULT_SIZE_PER_PAGE));
        ItemSummaryResponse response;
        if (user.isLoggedIn()) {
            response = itemService.getItemSummaryList(query, pageRequest, user.getId());
        } else {
            response = itemService.getItemSummaryList(query, pageRequest);
        }
        return ResponseEntity.ok(response);
    }
}