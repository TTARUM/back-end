package com.ttarum.item.controller;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemDetailResponse;
import com.ttarum.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ttarum.item.controller.utils.ItemConverter.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController implements IItemController {

    private final ItemService itemService;

    @Override
    @GetMapping
    public ResponseEntity<ItemDetailResponse> getDetail(final Long id) {
        Item item = itemService.getItem(id);
        return ResponseEntity.ok(convertToDetailResponse(item));
    }
}
