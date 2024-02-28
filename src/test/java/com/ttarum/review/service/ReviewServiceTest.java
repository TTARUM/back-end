package com.ttarum.review.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.exception.ItemNotFoundException;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Member;
import com.ttarum.member.exception.MemberNotFoundException;
import com.ttarum.member.repository.MemberRepository;
import com.ttarum.order.domain.Order;
import com.ttarum.order.exception.OrderNotFoundException;
import com.ttarum.order.repository.OrderRepository;
import com.ttarum.review.domain.Review;
import com.ttarum.review.domain.ReviewImage;
import com.ttarum.review.dto.request.ReviewCreationRequest;
import com.ttarum.review.dto.request.ReviewUpdateRequest;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.exception.ReviewException;
import com.ttarum.review.exception.ReviewNotFoundException;
import com.ttarum.review.repository.ReviewRepository;
import com.ttarum.review.validator.ReviewValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Spy
    ReviewValidator reviewValidator;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    ReviewService reviewService;

    @Test
    @DisplayName("제품의 PK 값으로 리뷰 목록을 조회할 수 있다.")
    void getReviewResponseList() {
        // given
        List<ReviewResponse> reviewResponseList = new ArrayList<>();
        reviewResponseList.add(ReviewResponse.builder().id(1L).build());
        reviewResponseList.add(ReviewResponse.builder().id(2L).build());

        List<ReviewImage> reviewImageList = new ArrayList<>();
        reviewImageList.add(ReviewImage.builder().review(Review.builder().id(1L).build()).order(1).build());
        reviewImageList.add(ReviewImage.builder().review(Review.builder().id(2L).build()).order(1).build());
        reviewImageList.add(ReviewImage.builder().review(Review.builder().id(2L).build()).order(2).build());
        reviewImageList.add(ReviewImage.builder().review(Review.builder().id(2L).build()).order(3).build());
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        when(reviewRepository.findReviewResponseByItemId(anyLong(), any(Pageable.class))).thenReturn(reviewResponseList);
        when(reviewRepository.findReviewImageByReviewId(anyList())).thenReturn(reviewImageList);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(Item.builder().id(1L).build()));

        List<ReviewResponse> result = reviewService.getReviewResponseList(1L, pageRequest);

        // then
        verify(reviewRepository, times(1)).findReviewResponseByItemId(anyLong(), any(Pageable.class));
        verify(reviewRepository, times(1)).findReviewImageByReviewId(anyList());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(0).getImageUrls()).hasSize(1);
        assertThat(result.get(1).getImageUrls()).hasSize(3);
    }

    @Test
    @DisplayName("올바르지 않은 제품의 PK 값을 받으면 예외가 발생한다.")
    void getReviewResponseListFailureByInvalidItemId() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        assertThatThrownBy(() -> reviewService.getReviewResponseList(-1L, pageRequest))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("자신의 리뷰를 제거할 수 있다.")
    void deleteReview() {
        // given
        long reviewId = 1;
        long memberId = 1;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Review review = Review.builder()
                .member(member)
                .build();

        // when
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        reviewService.deleteReview(reviewId, memberId);

        // then
        assertThat(review.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 리뷰를 제거할 경우 예외가 발생한다.")
    void deleteReviewFailureByInvalidReviewId() {
        // given
        long reviewId = 1;
        long memberId = 1;

        // when
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewId, memberId))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("다른 사람의 리뷰를 제거할 경우 예외가 발생한다.")
    void deleteReviewFailureByInvalidMemberId() {
        // given
        long reviewId = 1;
        long writerId = 1;
        long memberId = 2;
        Member member = Member.builder()
                .id(writerId)
                .build();
        Review review = Review.builder()
                .member(member)
                .build();

        // when
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewId, memberId))
                .isInstanceOf(ReviewException.class);
    }
    @Test
    @DisplayName("리뷰를 수정할 수 있다.")
    void updateReview() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();
        Review review = Review.builder()
                .id(1L)
                .content("content before updating")
                .member(member)
                .star(Integer.valueOf(1).shortValue())
                .isDeleted(false)
                .build();
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .content("content after updating")
                .rating(Integer.valueOf(3).shortValue())
                .build();

        // when
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        reviewService.updateReview(review.getId(), request, member.getId());

        // then
        assertThat(review.getContent()).isEqualTo(request.getContent());
        assertThat(review.getStar()).isEqualTo(request.getRating());
        verify(reviewRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("존재하지 않는 리뷰를 수정할 경우 예외가 발생한다.")
    void updateReviewFailureByInvalidReviewId() {
        // given
        long memberId = 1L;
        long reviewId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Review review = Review.builder()
                .id(reviewId)
                .content("content before updated")
                .member(member)
                .isDeleted(false)
                .build();
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .content("content after updated")
                .build();

        // when
        when(reviewRepository.findById(review.getId())).thenThrow(new ReviewNotFoundException());

        // then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, request, memberId))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("다른 사람의 리뷰를 수정할 경우 예외가 발생한다.")
    void updateReviewFailureByInvalidWriter() {
        // given
        long memberId = 1L;
        long reviewId = 1L;
        long anotherMemberId = 2L;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Review review = Review.builder()
                .id(reviewId)
                .content("content before updated")
                .member(member)
                .isDeleted(false)
                .build();
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .content("content after updated")
                .build();

        // when
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        // then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, request, anotherMemberId))
                .isInstanceOf(ReviewException.class)
                .hasMessage("사용자의 리뷰가 아닙니다.");
    }

    @Test
    @DisplayName("삭제된 리뷰를 수정할 경우 예외가 발생한다.")
    void updateReviewFailureByInvalidReview() {
        // given
        long memberId = 1L;
        long reviewId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Review review = Review.builder()
                .id(reviewId)
                .content("content before updated")
                .member(member)
                .isDeleted(true)
                .build();
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
                .content("content after updated")
                .build();

        // when
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, request, memberId))
                .isInstanceOf(ReviewException.class)
                .hasMessage("삭제된 리뷰는 수정이 불가능합니다.");
    }

    @Test
    @DisplayName("리뷰 작성")
    void createReview() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .build();
        ReviewCreationRequest request = ReviewCreationRequest.builder()
                .orderId(order.getId())
                .itemId(item.getId())
                .title("title")
                .content("content")
                .rating(Integer.valueOf(1).shortValue())
                .build();
        Review result = Review.builder()
                .id(1L)
                .member(member)
                .order(order)
                .item(item)
                .title(request.getTitle())
                .content(request.getContent())
                .star(request.getRating())
                .build();

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(reviewRepository.save(any(Review.class))).thenReturn(result);

        // when
        long reviewId = reviewService.createReview(member.getId(), request);

        // then
        verify(memberRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        assertThat(reviewId).isEqualTo(result.getId());
    }

    @Test
    @DisplayName("리뷰 작성 - 회원이 존재하지 않을 경우 예외가 발생한다.")
    void createReviewFailByInvalidMember() {
        // given
        long memberId = 1;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .build();
        ReviewCreationRequest request = ReviewCreationRequest.builder()
                .orderId(order.getId())
                .itemId(item.getId())
                .title("title")
                .content("content")
                .rating(Integer.valueOf(1).shortValue())
                .build();

        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(memberId, request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰 작성 - 주문이 존재하지 않을 경우 예외가 발생한다.")
    void createReviewFailByInvalidOrder() {
        // given
        long memberId = 1;
        long orderId = 1;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Order order = Order.builder()
                .id(orderId)
                .build();
        Item item = Item.builder()
                .id(1L)
                .build();
        ReviewCreationRequest request = ReviewCreationRequest.builder()
                .orderId(order.getId())
                .itemId(item.getId())
                .title("title")
                .content("content")
                .rating(Integer.valueOf(1).shortValue())
                .build();

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(memberId, request))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰 작성 - 제품이 존재하지 않을 경우 예외가 발생한다.")
    void createReviewFailByInvalidItem() {
        // given
        long memberId = 1;
        long itemId = 1;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(itemId)
                .build();
        ReviewCreationRequest request = ReviewCreationRequest.builder()
                .orderId(order.getId())
                .itemId(item.getId())
                .title("title")
                .content("content")
                .rating(Integer.valueOf(1).shortValue())
                .build();

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(itemRepository.findById(order.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(memberId, request))
                .isInstanceOf(ItemNotFoundException.class);
    }
}