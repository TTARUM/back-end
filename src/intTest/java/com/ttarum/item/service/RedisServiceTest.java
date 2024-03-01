package com.ttarum.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisServiceTest {
    @Autowired
    private RedisService redisService;

    @Test
    void testIncrementKeywordCount() {
        // given
        String keyword1 = "spring";
        String keyword2 = "boot";

        // when
        redisService.incrementKeywordCount(keyword1);
        redisService.incrementKeywordCount(keyword2);
        redisService.incrementKeywordCount(keyword2);

        List<ZSetOperations.TypedTuple<String>> list = redisService.getPopularKeywords(2).stream().toList();

        // then
        assertEquals(2, list.size());

        assertEquals(keyword2, list.get(0).getValue());
        assertEquals(2, list.get(0).getScore());

        assertEquals(keyword1, list.get(1).getValue());
        assertEquals(1, list.get(1).getScore());

        redisService.deleteAllKeywords();
    }
}
