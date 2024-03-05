package com.ttarum.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisServiceTest {
    @Autowired
    private RedisService redisService;

    @Test
    void testIncrementKeywordCount() {
        // given
        String keyCounted1 = "count1";
        String keyCounted2 = "count2";
        String keyCounted3 = "count3";

        // when
        redisService.incrementSearchKeywordCount(keyCounted1);

        redisService.incrementSearchKeywordCount(keyCounted3);
        redisService.incrementSearchKeywordCount(keyCounted3);
        redisService.incrementSearchKeywordCount(keyCounted3);

        redisService.incrementSearchKeywordCount(keyCounted2);
        redisService.incrementSearchKeywordCount(keyCounted2);

        List<String> list = redisService.getPopularSearchKeywords(3);

        // then
        assertEquals(3, list.size());
        assertEquals(keyCounted3, list.get(0));
        assertEquals(keyCounted2, list.get(1));
        assertEquals(keyCounted1, list.get(2));

        redisService.deleteAllSearchKeywords();
    }

    @Test
    void testIncreasePurchaseCount() {
        // given
        Long itemId1 = 1L;
        Long itemId2 = 2L;
        Long itemId3 = 3L;

        // when
        redisService.increasePurchaseCount(itemId1);

        redisService.increasePurchaseCount(itemId3);
        redisService.increasePurchaseCount(itemId3);
        redisService.increasePurchaseCount(itemId3);

        redisService.increasePurchaseCount(itemId2);
        redisService.increasePurchaseCount(itemId2);

        List<Long> list = redisService.getPopularPurchaseItems(3);

        // then
        assertEquals(3, list.size());
        assertEquals(itemId3, list.get(0));
        assertEquals(itemId2, list.get(1));
        assertEquals(itemId1, list.get(2));

        redisService.deleteAllPurchaseItems();
    }
}
