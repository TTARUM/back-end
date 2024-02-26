package com.ttarum.item.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.item.domain.Item;
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

import java.util.List;
import java.util.Optional;

import static com.ttarum.item.controller.utils.ItemConverter.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemControllerImpl implements ItemController {

    private final ItemService itemService;

    @Override
    @GetMapping
    public ResponseEntity<ItemDetailResponse> getDetail(final Long itemId) {
        Item item = itemService.getItem(itemId);
        return ResponseEntity.ok(convertToDetailResponse(item));
    }

    @Override
    @GetMapping("/list")
    public ResponseEntity<List<ItemSummaryResponse>> getSummary(
            @RequestParam(required = false) final String query,
            @VerificationUser final User user,
            @RequestParam final Optional<Integer> page,
            @RequestParam final Optional<Integer> size
    ) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(9));
        List<ItemSummaryResponse> itemSummaryList;
        if (user.isLoggedIn()) {
            itemSummaryList = itemService.getItemSummaryList(query, pageRequest, user.getId());
        } else {
            itemSummaryList = itemService.getItemSummaryList(query, pageRequest);
        }
        return ResponseEntity.ok(itemSummaryList);
    }
}