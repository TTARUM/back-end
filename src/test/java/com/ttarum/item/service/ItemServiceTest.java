package com.ttarum.item.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.exception.ItemException;
import com.ttarum.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

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
}