package com.ttarum.review.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.exception.ItemException;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.review.domain.Review;
import com.ttarum.review.domain.ReviewImage;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ReviewService reviewService;

    List<ReviewResponse> reviewResponseList;
    List<ReviewImage> reviewImageList;

    @BeforeEach
    void setUp() {
        reviewResponseList = new ArrayList<>();
        reviewResponseList.add(ReviewResponse.builder().id(1L).isOwnReview(true).build());
        reviewResponseList.add(ReviewResponse.builder().id(2L).build());

        reviewImageList = new ArrayList<>();
        reviewImageList.add(ReviewImage.builder().review(Review.builder().id(1L).build()).order(1).build());
        reviewImageList.add(ReviewImage.builder().review(Review.builder().id(2L).build()).order(2).build());
        reviewImageList.add(ReviewImage.builder().review(Review.builder().id(2L).build()).order(2).build());
        reviewImageList.add(ReviewImage.builder().review(Review.builder().id(2L).build()).order(2).build());
    }

    @Test
    @DisplayName("제품의 PK 값으로 리뷰 목록을 조회할 수 있다.")
    void getReviewResponseList() {
        when(reviewRepository.findReviewResponseByItemId(anyLong())).thenReturn(reviewResponseList);
        when(reviewRepository.findReviewImageByReviewId(anyList())).thenReturn(reviewImageList);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(Item.builder().id(1L).build()));

        List<ReviewResponse> result = reviewService.getReviewResponseList(1L);

        verify(reviewRepository, times(1)).findReviewResponseByItemId(anyLong());
        verify(reviewRepository, times(1)).findReviewImageByReviewId(anyList());

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(1, result.get(0).getImageUrls().size());
        assertEquals(3, result.get(1).getImageUrls().size());
    }

    @Test
    @DisplayName("제품의 PK 값으로 리뷰 목록을 조회할 수 있다.")
    void getReviewResponseListWithMemberId() {
        when(reviewRepository.findReviewResponseByItemId(anyLong(), anyLong())).thenReturn(reviewResponseList);
        when(reviewRepository.findReviewImageByReviewId(anyList())).thenReturn(reviewImageList);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(Item.builder().id(1L).build()));

        List<ReviewResponse> result = reviewService.getReviewResponseList(1L, 1L);

        verify(reviewRepository, times(1)).findReviewResponseByItemId(anyLong(), anyLong());
        verify(reviewRepository, times(1)).findReviewImageByReviewId(anyList());

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(1, result.get(0).getImageUrls().size());
        assertEquals(3, result.get(1).getImageUrls().size());
        assertTrue(result.get(0).isOwnReview());
    }

    @Test
    @DisplayName("올바르지 않은 제품의 PK 값을 받으면 예외가 발생한다.")
    void getEmptyReviewResponseListByInvalidItemId() {
        assertThatThrownBy(() -> reviewService.getReviewResponseList(-1L))
                .isInstanceOf(ItemException.class);
    }

}