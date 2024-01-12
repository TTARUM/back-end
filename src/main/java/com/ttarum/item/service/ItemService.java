package com.ttarum.item.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemSummaryResponse;
import com.ttarum.item.exception.ItemException;
import com.ttarum.item.repository.ItemRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    public Item getItem(final Long id) {
        return getItemById(id);
    }


    public List<ItemSummaryResponse> getItemSummaryList(final String name, final Long userId) {
        if (StringUtils.isEmpty(name)) {
            return List.of();
        }

        return itemRepository.getItemSummaryListByName(name, userId);
    }

    public List<ItemSummaryResponse> getItemSummaryList(final String name) {
        if (StringUtils.isEmpty(name)) {
            return List.of();
        }

        return itemRepository.getItemSummaryListByName(name);
    }

    private Item getItemById(final Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemException("아이템이 존재하지 않습니다."));
    }
}