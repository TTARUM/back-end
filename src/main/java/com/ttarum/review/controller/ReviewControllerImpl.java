package com.ttarum.review.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                                                                      @VerificationUser final User user,
                                                                      @RequestParam final Optional<Integer> page,
                                                                      @RequestParam final Optional<Integer> size) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(DEFAULT_SIZE));
        List<ReviewResponse> list;
        if (user.isLoggedIn()) {
            list = reviewService.getReviewResponseList(itemId, pageRequest, user.getId());
        } else {
            list = reviewService.getReviewResponseList(itemId, pageRequest);
        }
        return ResponseEntity.ok(list);
    }
}
