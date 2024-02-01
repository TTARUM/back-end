package com.ttarum.review.controller;

import com.ttarum.auth.domain.UserDetail;
import com.ttarum.common.exception.NoParameterException;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewControllerImpl implements ReviewController {

    private static final int DEFAULT_SIZE = 10;

    private final ReviewService reviewService;

    @GetMapping
    @Override
    public ResponseEntity<List<ReviewResponse>> getReviewResponseList(final Long itemId,
                                                                      @RequestParam final Optional<Integer> page,
                                                                      @RequestParam final Optional<Integer> size) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(DEFAULT_SIZE));
        List<ReviewResponse> list = reviewService.getReviewResponseList(itemId, pageRequest);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping
    @Override
    public ResponseEntity<Object> deleteReview(@RequestParam(name = "reviewId") final Optional<Long> optionalReviewId,
                                               @AuthenticationPrincipal final UserDetail user) {
        Long reviewId = optionalReviewId.orElseThrow(() -> new NoParameterException("reviewId는 필수값 입니다."));
        reviewService.deleteReview(reviewId, user.getId());
        return ResponseEntity.ok().build();
    }
}
