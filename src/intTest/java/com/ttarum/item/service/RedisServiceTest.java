package com.ttarum.item.service;

import com.ttarum.item.domain.redis.PopularItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RedisServiceTest {
    @Autowired
    private RedisService redisService;

    @Test
    void testIncrementKeywordCount() {
        // given
        String keyCounted1 = "count1";
        long itemId1 = 1;
        String keyCounted2 = "count2";
        long itemId2 = 2;
        String keyCounted3 = "count3";
        long itemId3 = 3;
        PopularItem item1 = new PopularItem(keyCounted1, itemId1);
        PopularItem item2 = new PopularItem(keyCounted2, itemId2);
        PopularItem item3 = new PopularItem(keyCounted3, itemId3);

        // when
        redisService.incrementSearchKeywordCount(keyCounted1, itemId1);

        redisService.incrementSearchKeywordCount(keyCounted3, itemId3);
        redisService.incrementSearchKeywordCount(keyCounted3, itemId3);
        redisService.incrementSearchKeywordCount(keyCounted3, itemId3);

        redisService.incrementSearchKeywordCount(keyCounted2, itemId2);
        redisService.incrementSearchKeywordCount(keyCounted2, itemId2);

        List<PopularItem> list = redisService.getPopularSearchKeywords(3);

        // then
        assertEquals(3, list.size());
        assertEquals(item3, list.get(0));
        assertEquals(item2, list.get(1));
        assertEquals(item1, list.get(2));

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
