package com.ttarum.review.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.review.dto.request.ReviewCreationRequest;
import com.ttarum.review.dto.request.ReviewUpdateRequest;
import com.ttarum.review.dto.response.ReviewCreationResponse;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.service.ReviewImageService;
import com.ttarum.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewControllerImpl implements ReviewController {

    private static final int PAGE_DEFAULT_SIZE = 10;

    private final ReviewService reviewService;
    private final ReviewImageService reviewImageService;

    @GetMapping
    @Override
    public ResponseEntity<List<ReviewResponse>> getReviewResponseList(final Long itemId,
                                                                      @RequestParam final Optional<Integer> page,
                                                                      @RequestParam final Optional<Integer> size) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(PAGE_DEFAULT_SIZE));
        List<ReviewResponse> list = reviewService.getReviewResponseList(itemId, pageRequest);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping
    @Override
    public ResponseEntity<Void> deleteReview(@RequestParam(name = "reviewId") final long reviewId,
                                             @AuthenticationPrincipal final CustomUserDetails user) {
        reviewService.deleteReview(reviewId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Override
    public ResponseEntity<Void> updateReview(final Long reviewId,
                                             @RequestBody final ReviewUpdateRequest request,
                                             @AuthenticationPrincipal final CustomUserDetails user) {
        reviewService.updateReview(reviewId, request, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @Override
    public ResponseEntity<ReviewCreationResponse> createReview(@AuthenticationPrincipal final CustomUserDetails user,
                                                               @RequestPart(name = "images", required = false) final List<MultipartFile> multipartFileList,
                                                               @RequestPart(name = "reviewCreationRequest") final ReviewCreationRequest reviewCreationRequest) {
        long reviewId = reviewService.createReview(user.getId(), reviewCreationRequest);
        if (Objects.nonNull(multipartFileList) && !multipartFileList.isEmpty()) {
            reviewImageService.saveImageList(reviewId, multipartFileList);
        }
        return ResponseEntity.ok(new ReviewCreationResponse(reviewId));
    }
}
