package com.ttarum.item.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemDetailResponse;
import com.ttarum.item.dto.response.ItemSimilarPriceResponse;
import com.ttarum.item.dto.response.ItemSummaryWithSimilarPrice;
import com.ttarum.item.dto.response.summary.ItemSummary;
import com.ttarum.item.dto.response.summary.ItemSummaryResponse;
import com.ttarum.item.exception.ItemNotFoundException;
import com.ttarum.item.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 제품의 상세정보를 반환합니다.
     *
     * @param itemId 제품의 Id 값
     * @return 제품의 상세정보가 담긴 {@link ItemDetailResponse}
     * @throws ItemNotFoundException 제품이 존재하지 않을 경우 발생합니다.
     */
    public ItemDetailResponse getItemDetail(final Long itemId) {
        Item item = getItemById(itemId);
        return ItemDetailResponse.builder()
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getItemImageUrl())
                .descriptionImageUrl(item.getItemDescriptionImageUrl())
                .build();
    }


    /**
     * 제품 이름으로 검색하여 제품에 대한 요약된 정보를 반환합니다.
     * 검색어가 비어있다면 전체 검색이 적용됩니다.
     *
     * @param query    검색어
     * @param pageable 페이징을 위한 정보가 담겨있는 객체
     * @return 검색된 제품 목록
     */
    public ItemSummaryResponse getItemSummaryList(final String query, final Pageable pageable) {
        List<ItemSummary> summaryList = itemRepository.getItemSummaryListByName(query, pageable);
        return new ItemSummaryResponse(summaryList);
    }

    /**
     * 제품 이름으로 검색하여 제품에 대한 요약된 정보를 반환합니다.
     * 로그인한 회원의 요청일 경우 호출됩니다.
     * 제품의 찜하기 목록 포함 여부를 포함합니다.
     * 검색어가 비어있다면 전체 검색이 적용됩니다.
     *
     * @param query    제품 이름
     * @param pageable 페이징을 위한 정보가 담겨있는 객체
     * @param userId   로그인한 회원의 Id 값
     * @return 검색된 제품 목록
     */
    public ItemSummaryResponse getItemSummaryList(final String query, final Pageable pageable, final Long userId) {
        List<ItemSummary> summaryList = itemRepository.getItemSummaryListByName(query, pageable, userId);
        return new ItemSummaryResponse(summaryList);
    }

    private Item getItemById(final Long id) {
        return itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
    }

    /**
     * 가격대가 비슷한 제품 조회 메서드
     *
     * @param price 가격대
     * @return 제품 리스트
     */
    public ItemSimilarPriceResponse getItemSummaryListWithSimilarPriceRange(final int price) {
        int lowPrice = getLowPrice(price);
        int highPrice = price + 10000;
        List<ItemSummaryWithSimilarPrice> summaryList = itemRepository.getItemSummaryWithSimilarPriceListByPriceRange(lowPrice, highPrice);
        return new ItemSimilarPriceResponse(summaryList);
    }

    private int getLowPrice(final int price) {
        int lowPrice;
        if (price < 10000)
            lowPrice = 0;
        else
            lowPrice = price - 10000;
        return lowPrice;
    }

    /**
     * 가격대가 비슷한 제품 조회 메서드
     * 로그인한 경우 사용되며 찜목록에 포함되었는지에 대한 여부가 포함됩니다.
     *
     * @param memberId 회원의 Id 값
     * @param price    가격대
     * @return 제품 리스트
     */
    public ItemSimilarPriceResponse getItemSummaryListWithSimilarPriceRange(final long memberId, final int price) {
        int lowPrice = getLowPrice(price);
        int highPrice = price + 10000;
        List<ItemSummaryWithSimilarPrice> summaryList = itemRepository.getItemSummaryWithSimilarPriceListByPriceRange(lowPrice, highPrice, memberId);
        return new ItemSimilarPriceResponse(summaryList);
    }
}