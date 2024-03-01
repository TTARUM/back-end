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

        List<String> list = redisService.getPopularSearchKeywordStrings(3);

        // then
        assertEquals(3, list.size());
        assertEquals(keyCounted3, list.get(0));
        assertEquals(keyCounted2, list.get(1));
        assertEquals(keyCounted1, list.get(2));

        redisService.deleteAllSearchKeywords();
    }
}
