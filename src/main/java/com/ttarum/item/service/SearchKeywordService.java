package com.ttarum.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class SearchKeywordService {
    private final StringRedisTemplate redisTemplate;
    private final String KEYWORD_ZSET_KEY = "searchKeywords";

    public void incrementKeywordCount(String keyword) {
        redisTemplate.opsForZSet().incrementScore(KEYWORD_ZSET_KEY, keyword, 1);
    }

    public List<String> getPopularKeywordStrings(int topN) {
        return getPopularKeywords(topN).stream().map(ZSetOperations.TypedTuple::getValue).toList();
    }

    public Set<ZSetOperations.TypedTuple<String>> getPopularKeywords(int topN) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(KEYWORD_ZSET_KEY, 0, topN - 1);
    }

    public void deleteAllKeywords() {
        redisTemplate.delete(KEYWORD_ZSET_KEY);
    }
}
