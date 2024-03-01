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

    /**
     * 검색 키워드 카운트 증가
     *
     * @param keyword 검색 키워드
     */
    public void incrementKeywordCount(String keyword) {
        redisTemplate.opsForZSet().incrementScore(KEYWORD_ZSET_KEY, keyword, 1);
    }

    /**
     * 인기 검색 키워드 목록 조회(정렬된 문자열만 조회)
     *
     * @param topN 조회할 인기 검색 키워드 개수
     * @return 인기 검색 키워드 목록
     */
    public List<String> getPopularKeywordStrings(int topN) {
        return getPopularKeywords(topN).stream().map(ZSetOperations.TypedTuple::getValue).toList();
    }

    /**
     * 인기 검색 키워드 목록 조회(점수와 함께 조회)
     *
     * @param topN 조회할 인기 검색 키워드 개수
     * @return 인기 검색 키워드 목록
     */
    public Set<ZSetOperations.TypedTuple<String>> getPopularKeywords(int topN) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(KEYWORD_ZSET_KEY, 0, topN - 1);
    }

    /**
     * 모든 검색 키워드 삭제
     * 사용 예시: 특정 기간이 자났을 때마다 초기화
     */
    public void deleteAllKeywords() {
        redisTemplate.delete(KEYWORD_ZSET_KEY);
    }
}
