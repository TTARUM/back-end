package com.ttarum.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

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
    void getPopularItemSummaryListInCategory() {
        // given
        List<Long> itemIdList = List.of(1L, 2L, 3L);

        // when
        itemRepository.getPopularItemSummaryListInCategory(itemIdList);
    }
}