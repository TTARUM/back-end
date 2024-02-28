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
import com.ttarum.review.dto.response.ReviewImageResponse;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.exception.ReviewException;
import com.ttarum.review.exception.ReviewNotFoundException;
import com.ttarum.review.repository.ReviewRepository;
import com.ttarum.review.validator.ReviewValidator;
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
    private final ReviewValidator reviewValidator;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    /**
     * 특정 제품의 리뷰들을 반환합니다.
     *
     * @param itemId   특정 제품의 Id 값
     * @param pageable pageable
     * @return 특정 제품의 리뷰 리스트
     * @throws ItemNotFoundException 특정 제품이 존재하지 않을 경우 발생합니다.
     */
    public List<ReviewResponse> getReviewResponseList(final Long itemId, final Pageable pageable) {
        checkItemExistence(itemId);
        List<ReviewResponse> reviewResponseList = reviewRepository.findReviewResponseByItemId(itemId, pageable);
        List<Long> ids = extractId(reviewResponseList);

        List<ReviewImage> reviewImageList = reviewRepository.findReviewImageByReviewId(ids);
        reviewImageList.forEach(ri ->
                reviewResponseList.stream()
                        .filter(r -> r.getId().equals(ri.getReview().getId()))
                        .findFirst()
                        .orElseThrow(() -> new ReviewException(HttpStatus.INTERNAL_SERVER_ERROR, "Unreachable Exception"))
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

    /**
     * {@link Long reviewId}가 Id 값인 리뷰를 제거합니다.
     * {@link Long memberId}가 Id 값인 회원의 리뷰인 경우에만 제거됩니다.
     *
     * @param reviewId 제거할 리뷰의 Id 값
     * @param memberId 회원의 Id 값
     * @throws ReviewNotFoundException {@link Long reviewId}가 Id 값인 리뷰가 없을 경우 발생합니다.
     * @throws ReviewException         회원의 리뷰가 아닌 경우 발생합니다.
     */
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

    /**
     * {@link Long reviewId}가 Id 값인 리뷰를 업데이트합니다.
     * {@link Long memberId}가 Id 값인 회원만 업데이트할 수 있으며, 삭제된 리뷰의 경우 업데이트가 불가능합니다.
     * 리뷰의 이미지는 업데이트가 불가능합니다.
     *
     * @param reviewId 업데이트할 리뷰의 Id 값
     * @param request  업데이트할 데이터가 담긴 객체
     * @param memberId 회원의 Id 값
     * @throws ReviewNotFoundException {@link Long reviewId}가 Id 값인 리뷰가 없을 경우 발생합니다.
     * @throws ReviewException         삭제된 리뷰의 업데이트를 시도할 경우, {@link Long memberId}가 Id 값인 회원이 작성한 리뷰가 아닌 경우 발생합니다.
     */
    @Transactional
    public void updateReview(final Long reviewId, final ReviewUpdateRequest request, final Long memberId) {
        Review review = getReviewById(reviewId);
        if (Boolean.TRUE.equals(review.getIsDeleted())) {
            throw new ReviewException(HttpStatus.BAD_REQUEST, "삭제된 리뷰는 수정이 불가능합니다.");
        }
        validateWriter(review, memberId);
        review.update(request, reviewValidator);
    }

    private void validateWriter(final Review review, final Long memberId) {
        if (!review.getMember().getId().equals(memberId)) {
            throw new ReviewException(HttpStatus.FORBIDDEN, "사용자의 리뷰가 아닙니다.");
        }
    }

    /**
     * 리뷰를 생성하고 저장한다.
     *
     * @param memberId              리뷰를 작성한 회원의 Id 값
     * @param reviewCreationRequest 작성할 리뷰의 데이터
     * @return 생성된 리뷰의 Id 값
     */
    @Transactional
    public long createReview(final Long memberId, final ReviewCreationRequest reviewCreationRequest) {
        Member member = getMemberById(memberId);
        Order order = getOrderById(reviewCreationRequest.getOrderId());
        Item item = getItemById(reviewCreationRequest.getItemId());

        Review review = reviewCreationRequest.toReviewEntity();
        review.setInitialForeignEntity(member, order, item);
        return reviewRepository.save(review).getId();
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Order getOrderById(final long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    private Item getItemById(final long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);
    }
}
