package com.ttarum.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SearchKeywordServiceTest {
    @Autowired
    private SearchKeywordService searchKeywordService;

    @Test
    void testIncrementKeywordCount() {
        // given
        String keyword1 = "spring";
        String keyword2 = "boot";

        // when
        searchKeywordService.incrementKeywordCount(keyword1);
        searchKeywordService.incrementKeywordCount(keyword2);
        searchKeywordService.incrementKeywordCount(keyword2);

        List<ZSetOperations.TypedTuple<String>> list = searchKeywordService.getPopularKeywords(2).stream().toList();

        // then
        assertEquals(2, list.size());

        assertEquals(keyword2, list.get(0).getValue());
        assertEquals(2, list.get(0).getScore());

        assertEquals(keyword1, list.get(1).getValue());
        assertEquals(1, list.get(1).getScore());

        searchKeywordService.deleteAllKeywords();
    }
}
