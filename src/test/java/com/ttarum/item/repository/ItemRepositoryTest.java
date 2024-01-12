package com.ttarum.item.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("SQL 쿼리를 확인한다.")
    void getItemSummaryListByName() {
        String userName = "a";
        itemRepository.getItemSummaryListByName(userName);
    }
}