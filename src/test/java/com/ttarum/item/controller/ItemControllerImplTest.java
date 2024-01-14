package com.ttarum.item.controller;

import com.ttarum.common.dto.user.LoggedInUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.item.controller.advice.ItemControllerAdvice;
import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemSummaryResponse;
import com.ttarum.item.exception.ItemException;
import com.ttarum.item.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerImplTest {

    @InjectMocks
    private ItemControllerImpl itemControllerImpl;

    @Mock
    private ItemService itemService;

    private User user;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemControllerImpl)
                .setControllerAdvice(new ItemControllerAdvice())
                .build();
        user = LoggedInUser.builder()
                .id(1L)
                .build();
    }

    @Test
    @DisplayName("아이템의 아이디를 이용해 아이템의 상세정보를 가져올 수 있다.")
    void getDetail() throws Exception {
        Item item = Item.builder()
                .id(1L)
                .name("와인")
                .description("와인 설명")
                .price(13000)
                .itemImageUrl("imageUrl")
                .itemDescriptionImageUrl("descriptionImageUrl")
                .build();
        doReturn(item).when(itemService).getItem(1L);

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/items")
                        .param("id", "1")
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("name", item.getName()).exists())
                .andExpect(jsonPath("description", item.getDescription()).exists())
                .andExpect(jsonPath("price", item.getPrice()).exists())
                .andExpect(jsonPath("imageUrl", item.getItemImageUrl()).exists())
                .andExpect(jsonPath("descriptionImageUrl", item.getItemDescriptionImageUrl()).exists());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("존재하지 않는 아이템을 찾을 경우")
    void getDetailFailureByInvalidId() throws Exception {

        when(itemService.getItem(0L)).thenThrow(new ItemException("아이템이 존재하지 않습니다."));

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/items")
                        .param("id", "0")
        );

        resultActions.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("dateTime").exists())
                .andExpect(jsonPath("message", "아이템이 존재하지 않습니다.").exists())
                .andExpect(result -> assertEquals(result.getResolvedException().getClass(), ItemException.class));
    }

    @Test
    @DisplayName("아이템의 이름으로 아이템을 검색할 수 있다.")
    void getSummary() {
        // given
        String name = "testName";
        List<ItemSummaryResponse> itemList = List.of(
                ItemSummaryResponse.builder().name("testName").categoryName("testCategory").price(13000).imageUrl("/Home/image").rating(3.4).build(),
                ItemSummaryResponse.builder().name("testName2").categoryName("testCategory2").price(13000).imageUrl("/Home/image").rating(4.6).build()
        );
        PageRequest pageRequest = PageRequest.of(0, 10);
        given(itemService.getItemSummaryList(name, pageRequest, user.getId())).willReturn(itemList);

        // when
        ResponseEntity<List<ItemSummaryResponse>> response = itemControllerImpl.getSummary(name, user, Optional.of(0), Optional.of(10));

        // then
        verify(itemService).getItemSummaryList(name, pageRequest, user.getId());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(itemList);
    }

    @Test
    @DisplayName("아이템의 이름이 null일 경우")
    void getSummaryValueIsNull() {
        // given
        String name = null;
        PageRequest pageRequest = PageRequest.of(0, 10);
        given(itemService.getItemSummaryList(name, pageRequest, user.getId())).willReturn(List.of());

        // when
        ResponseEntity<List<ItemSummaryResponse>> response = itemControllerImpl.getSummary(name, user, Optional.of(0), Optional.of(10));

        // then
        verify(itemService).getItemSummaryList(name, pageRequest, user.getId());
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("아이템의 이름이 비어있을 경우")
    void getSummaryValueIsEmpty() {
        // given
        String name = "";
        PageRequest pageRequest = PageRequest.of(0, 10);
        given(itemService.getItemSummaryList(name, pageRequest, user.getId())).willReturn(List.of());

        // when
        ResponseEntity<List<ItemSummaryResponse>> response = itemControllerImpl.getSummary(name, user, Optional.of(0), Optional.of(10));

        // then
        verify(itemService).getItemSummaryList(name, pageRequest, user.getId());
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEmpty();
    }
}