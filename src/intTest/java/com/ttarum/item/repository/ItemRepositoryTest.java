package com.ttarum.item.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("SQL 쿼리를 확인한다.")
    void getItemSummaryListByName() {
        String userName = "a";
        PageRequest pageRequest = PageRequest.of(0, 10);
        itemRepository.getItemSummaryListByName(userName, pageRequest);
    }
}