package com.ttarum.review.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Override
    public ResponseEntity<List<ReviewResponse>> getReviewResponseList(final Long itemId, @VerificationUser final User user) {
        List<ReviewResponse> list;
        if (user.isLoggedIn()) {
            list = reviewService.getReviewResponseList(itemId, user.getId());
        } else {
            list = reviewService.getReviewResponseList(itemId);
        }
        return ResponseEntity.ok(list);
    }
}
