package com.ttarum.item.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemSummaryResponse;
import com.ttarum.item.exception.ItemException;
import com.ttarum.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Test
    @DisplayName("아이템의 아이디로 아이템을 찾을 수 있다.")
    void getItem() {
        Item item = Item.builder()
                .id(1L)
                .name("와인")
                .description("와인 설명")
                .price(13000)
                .itemImageUrl("/Home/image/image/imageName.jpg")
                .itemDescriptionImageUrl("/Home/image/description/imageName.jpg")
                .build();
        doReturn(Optional.of(item)).when(itemRepository).findById(1L);

        Item foundItem = itemService.getItem(1L);

        assertThat(foundItem.getId()).isEqualTo(item.getId());
        assertThat(foundItem.getName()).isEqualTo(item.getName());
        assertThat(foundItem.getDescription()).isEqualTo(item.getDescription());
        assertThat(foundItem.getPrice()).isEqualTo(item.getPrice());
        assertThat(foundItem.getItemImageUrl()).isEqualTo(item.getItemImageUrl());
        assertThat(foundItem.getItemDescriptionImageUrl()).isEqualTo(item.getItemDescriptionImageUrl());
    }

    @Test
    @DisplayName("존재하지 않는 아이템을 찾을 경우 예외가 발생한다.")
    void getItemFailureByInvalidId() {
        doReturn(Optional.empty()).when(itemRepository).findById(0L);

        assertThatThrownBy(() -> itemService.getItem(0L))
                .isInstanceOf(ItemException.class)
                .hasMessage("아이템이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("아이템 이름으로 아이템을 조회할 수 있다.")
    void getItemSummary() {
        // given
        ItemSummaryResponse element = new ItemSummaryResponse(1, "sample", "sample", 13000, 2.3, "/Home/image", false, Instant.now(), 1);
        List<ItemSummaryResponse> list = List.of(element);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        when(itemRepository.getItemSummaryListByName("sample", pageRequest)).thenReturn(list);
        List<ItemSummaryResponse> response = itemService.getItemSummaryList("sample", pageRequest);

        // then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getId()).isEqualTo(element.getId());
        assertThat(response.get(0).getName()).isEqualTo(element.getName());
        assertThat(response.get(0).getPrice()).isEqualTo(element.getPrice());
        assertThat(response.get(0).getRating()).isEqualTo(element.getRating());
        assertThat(response.get(0).getImageUrl()).isEqualTo(element.getImageUrl());
        assertThat(response.get(0).getCategoryName()).isEqualTo(element.getCategoryName());
        assertThat(response.get(0).getCreatedAt()).isEqualTo(element.getCreatedAt());
        assertThat(response.get(0).getSalesVolume()).isEqualTo(element.getSalesVolume());
    }

    @Test
    @DisplayName("아이템 이름으로 빈 문자열이 오면 빈 리스트를 반환한다.")
    void getItemSummaryEmptyValue() {
        // given
        String name = "";
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        List<ItemSummaryResponse> response = itemService.getItemSummaryList(name, pageRequest);

        // then
        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("아이템 이름으로 널 값이 오면 빈 리스트를 반환한다.")
    void getItemSummaryNullValue() {
        // given
        String name = null;
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        List<ItemSummaryResponse> response = itemService.getItemSummaryList(name, pageRequest);

        // then
        assertThat(response).isEmpty();
    }
}