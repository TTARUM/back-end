package com.ttarum.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final String SEARCH_KEYWORD_ZSET_KEY = "searchKeywords";
    private final String PURCHASE_ITEM_ZSET_KEY = "purchaseItems";

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
        return getZsetSearchKeywords(topN).stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList();
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

    /**
     * 상품 구매 카운트 증가
     *
     * @param itemId 상품 아이디
     */
    public void increasePurchaseCount(Long itemId) {
        redisTemplate.opsForZSet().incrementScore(PURCHASE_ITEM_ZSET_KEY, itemId.toString(), 1);
    }

    /**
     * 인기 상품 목록 조회
     *
     * @param topN 조회할 인기 상품 개수
     * @return 인기 상품 목록
     */
    public List<Long> getPopularPurchaseItems(int topN) {
        return getZsetPurchaseItems(topN).stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)   // it could not be null
                .map(Long::parseLong)
                .toList();
    }

    private Set<ZSetOperations.TypedTuple<String>> getZsetPurchaseItems(int topN) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(PURCHASE_ITEM_ZSET_KEY, 0, topN - 1);
    }

    /**
     * 모든 상품 구매 카운트 삭제
     */
    public void deleteAllPurchaseItems() {
        redisTemplate.delete(PURCHASE_ITEM_ZSET_KEY);
    }
}
