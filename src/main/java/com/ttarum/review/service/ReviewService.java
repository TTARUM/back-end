package com.ttarum.review.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.exception.ItemNotFoundException;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.review.domain.Review;
import com.ttarum.review.domain.ReviewImage;
import com.ttarum.review.dto.request.ReviewUpdateRequest;
import com.ttarum.review.dto.response.ReviewImageResponse;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.exception.ReviewException;
import com.ttarum.review.exception.ReviewNotFoundException;
import com.ttarum.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;

    public List<ReviewResponse> getReviewResponseList(final Long itemId, final Pageable pageable) {
        checkItemExistence(itemId);
        List<ReviewResponse> reviewResponseList = reviewRepository.findReviewResponseByItemId(itemId, pageable);
        List<Long> ids = extractId(reviewResponseList);

        List<ReviewImage> reviewImageList = reviewRepository.findReviewImageByReviewId(ids);
        reviewImageList.forEach(ri ->
                reviewResponseList.stream()
                        .filter(r -> r.getId().equals(ri.getReview().getId()))
                        .findFirst()
                        .orElseThrow(() -> new ReviewException(HttpStatus.BAD_REQUEST, "Unreachable Exception"))
                        .addImageUrl(ReviewImageResponse.of(ri))
        );

        return reviewResponseList;
    }

    private List<Long> extractId(final List<ReviewResponse> reviewResponseList) {
        return reviewResponseList.stream()
                .map(ReviewResponse::getId)
                .toList();
    }

    private void checkItemExistence(final long itemId) {
        Optional<Item> foundItem = itemRepository.findById(itemId);
        if (foundItem.isEmpty()) {
            throw new ItemNotFoundException();
        }
    }

    @Transactional
    public void deleteReview(final Long reviewId, final Long memberId) {
        Review review = getReviewById(reviewId);
        validateWriter(review, memberId);
        review.delete();
    }

    private Review getReviewById(final Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);
    }

    @Transactional
    public void updateReview(final Long reviewId, final ReviewUpdateRequest request, final Long memberId) {
        Review review = getReviewById(reviewId);
        if (Boolean.TRUE.equals(review.getIsDeleted())) {
            throw new ReviewException(HttpStatus.BAD_REQUEST, "삭제된 리뷰는 수정이 불가능합니다.");
        }
        validateWriter(review, memberId);
        review.update(request);
    }

    private void validateWriter(final Review review, final Long memberId) {
        if (!review.getMember().getId().equals(memberId)) {
            throw new ReviewException(HttpStatus.FORBIDDEN, "사용자의 리뷰가 아닙니다.");
        }
    }
}
