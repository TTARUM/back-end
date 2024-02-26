package com.ttarum.item.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemDetailResponse;
import com.ttarum.item.dto.response.ItemSummaryResponse;
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
    public List<ItemSummaryResponse> getItemSummaryList(final String query, final Pageable pageable) {
        return itemRepository.getItemSummaryListByName(query, pageable);
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
    public List<ItemSummaryResponse> getItemSummaryList(final String query, final Pageable pageable, final Long userId) {
        return itemRepository.getItemSummaryListByName(query, pageable, userId);
    }

    private Item getItemById(final Long id) {
        return itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
    }
}