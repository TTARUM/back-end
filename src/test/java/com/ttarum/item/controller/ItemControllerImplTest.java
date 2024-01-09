package com.ttarum.item.controller;

import com.ttarum.item.controller.advice.ItemControllerAdvice;
import com.ttarum.item.domain.Item;
import com.ttarum.item.exception.ItemException;
import com.ttarum.item.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerImplTest {

    @InjectMocks
    private ItemControllerImpl itemControllerImpl;

    @Mock
    private ItemService itemService;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemControllerImpl)
                .setControllerAdvice(new ItemControllerAdvice())
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
}