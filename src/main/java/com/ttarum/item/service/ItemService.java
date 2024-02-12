package com.ttarum.item.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemSummaryResponse;
import com.ttarum.item.exception.ItemException;
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

    public Item getItem(final Long id) {
        return getItemById(id);
    }


    /**
     * 제품 이름으로 검색하여 요약된 제품에 대한 정보 반환
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
     * 제품 이름으로 검색하여 요약된 제품에 대한 정보 반환
     * 로그인한 사용자의 찜하기 목록에 포함된 제품에 대한 여부를 포함한다.
     * 검색어가 비어있다면 전체 검색이 적용됩니다.
     *
     * @param query    제품 이름
     * @param pageable 페이징을 위한 정보가 담겨있는 객체
     * @param userId   로그인한 사용자의 PK 값
     * @return 검색된 제품 목록
     */
    public List<ItemSummaryResponse> getItemSummaryList(final String query, final Pageable pageable, final Long userId) {
        return itemRepository.getItemSummaryListByName(query, pageable, userId);
    }

    private Item getItemById(final Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemException("아이템이 존재하지 않습니다."));
    }
}