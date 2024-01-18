package com.ttarum.review.controller;

import com.ttarum.common.annotation.resolver.VerificationUserResolver;
import com.ttarum.common.filter.UserVerificationFilter;
import com.ttarum.review.controller.advice.ReviewControllerAdvice;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.service.ReviewService;
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

import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReviewControllerImplTest {

    @Mock
    ReviewService reviewService;

    @InjectMocks
    ReviewControllerImpl reviewControllerImpl;

    MockMvc mockMvc;

    @InjectMocks
    UserVerificationFilter filter;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewControllerImpl)
                .setControllerAdvice(new ReviewControllerAdvice())
                .addFilter(filter)
                .setCustomArgumentResolvers(new VerificationUserResolver())
                .build();
    }

    @Test
    @DisplayName("제품의 PK 값으로 제품의 리뷰를 조회할 수 있다.")
    void getReviewResponseList() throws Exception {
        // given
        Long itemId = 1L;
        List<ReviewResponse> reviewResponses = List.of(new ReviewResponse(1L, "nickname", "content", (short) 1, Instant.now(), false));
        given(reviewService.getReviewResponseList(anyLong())).willReturn(reviewResponses);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/reviews")
                        .param("itemId", String.valueOf(itemId))
        );

        // then
        verify(reviewService, times(1)).getReviewResponseList(itemId);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$.[?(@.nickname == '%s')]", "nickname").exists())
                .andExpect(jsonPath("$.[?(@.content == '%s')]", "content").exists())
                .andExpect(jsonPath("$.[?(@.rating == '%d')]", (short) 1).exists());
    }

}