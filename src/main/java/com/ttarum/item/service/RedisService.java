package com.ttarum.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final String SEARCH_KEYWORD_ZSET_KEY = "searchKeywords";

    /**
     * 검색 키워드 카운트 증가
     *
     * @param keyword 검색 키워드
     */
    public void incrementSearchKeywordCount(String keyword) {
        redisTemplate.opsForZSet().incrementScore(SEARCH_KEYWORD_ZSET_KEY, keyword, 1);
    }

    /**
     * 인기 검색 키워드 목록 조회(정렬된 문자열만 조회)
     *
     * @param topN 조회할 인기 검색 키워드 개수
     * @return 인기 검색 키워드 목록
     */
    public List<String> getPopularSearchKeywords(int topN) {
        return getZsetSearchKeywords(topN).stream().map(ZSetOperations.TypedTuple::getValue).toList();
    }
    private Set<ZSetOperations.TypedTuple<String>> getZsetSearchKeywords(int topN) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(SEARCH_KEYWORD_ZSET_KEY, 0, topN - 1);
    }

    /**
     * 모든 검색 키워드 삭제
     * 사용 예시: 특정 기간이 자났을 때마다 초기화
     */
    public void deleteAllSearchKeywords() {
        redisTemplate.delete(SEARCH_KEYWORD_ZSET_KEY);
    }
}
