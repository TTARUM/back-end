package com.ttarum.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    void getItemSummaryListByName() {
        String query = "a";
        PageRequest pageRequest = PageRequest.of(0, 10);
        itemRepository.getItemSummaryListByName(query, pageRequest);
    }

    @Test
    void getItemSummaryWithSimilarPriceListByPriceRange() {
        // given
        long memberId = 1;
        int lowPrice = 10000;
        int highPrice = 20000;
        PageRequest pageRequest = PageRequest.of(0, 7);

        // when
        itemRepository.getItemSummaryWithSimilarPriceListByPriceRange(lowPrice, highPrice, memberId, pageRequest);
    }
}