package com.ttarum.review.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.review.domain.ReviewImage;
import com.ttarum.review.dto.response.ReviewImageResponse;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.exception.ReviewException;
import com.ttarum.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;

    public List<ReviewResponse> getReviewResponseList(final Long itemId, final Long userId) {
        getItem(itemId);
        List<ReviewResponse> reviewResponseList = reviewRepository.findReviewResponseByItemId(itemId, userId);
        List<Long> ids = extractId(reviewResponseList);

        List<ReviewImage> reviewImageList = reviewRepository.findReviewImageByReviewId(ids);
        reviewImageList.forEach(ri ->
                reviewResponseList.stream()
                        .filter(r -> r.getId().equals(ri.getReview().getId()))
                        .findFirst()
                        .orElseThrow(() -> new ReviewException("Unreachable Exception"))
                        .addImageUrl(ReviewImageResponse.of(ri))
        );
        return reviewResponseList;
    }

    public List<ReviewResponse> getReviewResponseList(final Long itemId) {
        getItem(itemId);
        List<ReviewResponse> reviewResponseList = reviewRepository.findReviewResponseByItemId(itemId);
        List<Long> ids = extractId(reviewResponseList);

        List<ReviewImage> reviewImageList = reviewRepository.findReviewImageByReviewId(ids);
        reviewImageList.forEach(ri ->
                reviewResponseList.stream()
                        .filter(r -> r.getId().equals(ri.getReview().getId()))
                        .findFirst()
                        .orElseThrow(() -> new ReviewException("Unreachable Exception"))
                        .addImageUrl(ReviewImageResponse.of(ri))
        );
        return reviewResponseList;
    }

    private List<Long> extractId(final List<ReviewResponse> reviewResponseList) {
        return reviewResponseList.stream()
                .map(ReviewResponse::getId)
                .toList();
    }

    private Item getItem(final Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ReviewException("해당 제품을 찾을 수 없습니다."));
    }
}
