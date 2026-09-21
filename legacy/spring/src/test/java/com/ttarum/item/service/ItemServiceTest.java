package com.ttarum.item.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemDetailResponse;
import com.ttarum.item.dto.response.ItemSimilarPriceResponse;
import com.ttarum.item.dto.response.ItemSummaryWithSimilarPrice;
import com.ttarum.item.dto.response.summary.ItemSummary;
import com.ttarum.item.dto.response.summary.ItemSummaryResponse;
import com.ttarum.item.exception.ItemNotFoundException;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.order.repository.OrderRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    ItemService itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    OrderRepository orderRepository;

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

        ItemDetailResponse itemDetail = itemService.getItemDetail(1L);

        assertThat(itemDetail.getName()).isEqualTo(item.getName());
        assertThat(itemDetail.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDetail.getPrice()).isEqualTo(item.getPrice());
        assertThat(itemDetail.getImageUrl()).isEqualTo(item.getItemImageUrl());
        assertThat(itemDetail.getDescriptionImageUrl()).isEqualTo(item.getItemDescriptionImageUrl());
    }

    @Test
    @DisplayName("존재하지 않는 아이템을 찾을 경우 예외가 발생한다.")
    void getItemFailureByInvalidId() {
        doReturn(Optional.empty()).when(itemRepository).findById(0L);

        assertThatThrownBy(() -> itemService.getItemDetail(0L))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("아이템 이름으로 아이템을 조회할 수 있다.")
    void getItemSummary() {
        // given
        ItemSummary element = new ItemSummary(1, "sample", "sample", 13000, 2.3, "/Home/image", false, Instant.now(), 1);
        List<ItemSummary> list = List.of(element);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        when(itemRepository.getItemSummaryListByName("sample", pageRequest)).thenReturn(list);
        ItemSummaryResponse response = itemService.getItemSummaryList("sample", pageRequest);
        List<ItemSummary> summaryList = response.getItemSummaryResponseList();

        // then
        assertThat(summaryList).hasSize(1);
        assertThat(summaryList.get(0).getId()).isEqualTo(element.getId());
        assertThat(summaryList.get(0).getName()).isEqualTo(element.getName());
        assertThat(summaryList.get(0).getPrice()).isEqualTo(element.getPrice());
        assertThat(summaryList.get(0).getRating()).isEqualTo(element.getRating());
        assertThat(summaryList.get(0).getImageUrl()).isEqualTo(element.getImageUrl());
        assertThat(summaryList.get(0).getCategoryName()).isEqualTo(element.getCategoryName());
        assertThat(summaryList.get(0).getCreatedAt()).isEqualTo(element.getCreatedAt());
        assertThat(summaryList.get(0).getOrderCount()).isEqualTo(element.getOrderCount());
    }

    @Test
    @DisplayName("아이템 이름으로 빈 문자열이 오면 빈 리스트를 반환한다.")
    void getItemSummaryEmptyValue() {
        // given
        String name = "";
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        ItemSummaryResponse response = itemService.getItemSummaryList(name, pageRequest);
        List<ItemSummary> summaryList = response.getItemSummaryResponseList();

        // then
        assertThat(summaryList).isEmpty();
    }

    @Test
    @DisplayName("아이템 이름으로 널 값이 오면 빈 리스트를 반환한다.")
    void getItemSummaryNullValue() {
        // given
        String name = null;
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        ItemSummaryResponse response = itemService.getItemSummaryList(name, pageRequest);
        List<ItemSummary> summaryList = response.getItemSummaryResponseList();

        // then
        assertThat(summaryList).isEmpty();
    }

    @Test
    @DisplayName("가격대가 비슷한 술 조회")
    void getItemSummaryListWithSimilarPriceRange() {
        // given
        int price = 23000;
        List<ItemSummaryWithSimilarPrice> itemSummaryList = List.of(
                ItemSummaryWithSimilarPrice.builder()
                        .itemId(1)
                        .itemName("item1")
                        .price(17000)
                        .imageUrl("ttarum.image.url")
                        .inWishList(false)
                        .build()
        );
        PageRequest pageRequest = PageRequest.of(0, 7);

        when(itemRepository.getItemSummaryWithSimilarPriceListByPriceRange(price - 10000, price + 10000, pageRequest)).thenReturn(itemSummaryList);

        // when
        ItemSimilarPriceResponse response = itemService.getItemSummaryListWithSimilarPriceRange(price, pageRequest);
        List<ItemSummaryWithSimilarPrice> list = response.getItemSummaryList();

        // then
        verify(itemRepository, times(1)).getItemSummaryWithSimilarPriceListByPriceRange(price - 10000, price + 10000, pageRequest);
        assertThat(list).size().isEqualTo(1);
        assertThat(list.get(0).getItemId()).isEqualTo(1);
        assertThat(list.get(0).getItemName()).isEqualTo("item1");
        assertThat(list.get(0).getPrice()).isEqualTo(17000);
        assertThat(list.get(0).getImageUrl()).isEqualTo("ttarum.image.url");
        assertThat(list.get(0).isInWishList()).isFalse();
    }

    @Test
    @DisplayName("가격대가 비슷한 술 조회 - 10000원 이하일 경우")
    void getItemSummaryListWithSimilarPriceRange_PriceLessThan10000() {
        // given
        long memberId = 1;
        int price = 7000;
        List<ItemSummaryWithSimilarPrice> itemSummaryList = List.of(
                ItemSummaryWithSimilarPrice.builder()
                        .itemId(1)
                        .itemName("item1")
                        .price(17000)
                        .imageUrl("ttarum.image.url")
                        .inWishList(true)
                        .build()
        );
        PageRequest pageRequest = PageRequest.of(0, 7);

        when(itemRepository.getItemSummaryWithSimilarPriceListByPriceRange(0, price + 10000, memberId, pageRequest)).thenReturn(itemSummaryList);

        // when
        ItemSimilarPriceResponse response = itemService.getItemSummaryListWithSimilarPriceRange(memberId, price, pageRequest);
        List<ItemSummaryWithSimilarPrice> list = response.getItemSummaryList();

        // then
        verify(itemRepository, times(1)).getItemSummaryWithSimilarPriceListByPriceRange(0, price + 10000, memberId, pageRequest);
        assertThat(list).size().isEqualTo(1);
        assertThat(list.get(0).getItemId()).isEqualTo(1);
        assertThat(list.get(0).getItemName()).isEqualTo("item1");
        assertThat(list.get(0).getPrice()).isEqualTo(17000);
        assertThat(list.get(0).getImageUrl()).isEqualTo("ttarum.image.url");
        assertThat(list.get(0).isInWishList()).isTrue();
    }
}