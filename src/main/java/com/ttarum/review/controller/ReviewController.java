package com.ttarum.review.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.review.dto.request.ReviewCreationRequest;
import com.ttarum.review.dto.request.ReviewUpdateRequest;
import com.ttarum.review.dto.response.ReviewCreationResponse;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.dto.response.ReviewUpdateResponse;
import com.ttarum.review.service.ReviewImageService;
import com.ttarum.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name = "review", description = "리뷰")
public class ReviewController {

    private static final int PAGE_DEFAULT_SIZE = 10;

    private final ReviewService reviewService;
    private final ReviewImageService reviewImageService;

    /**
     * 특정 제품에 대한 리뷰 조회
     *
     * @param itemId 특정 제품의 Id 값
     * @param page   페이지 넘버
     * @param size   페이지당 반환되는 리뷰의 개수
     * @return 리뷰 목록
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Operation(summary = "특정 제품에 대한 리뷰 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 제품 조회")
    })
    @Parameters(value = {
            @Parameter(name = "itemId", description = "제품의 PK 값", example = "1"),
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "0"),
            @Parameter(name = "size", description = "한 페이지당 리뷰 수 (기본 값 10개)", example = "5")

    })
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviewResponseList(
            @RequestParam final long itemId,
            final Optional<Integer> page,
            final Optional<Integer> size
    ) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(PAGE_DEFAULT_SIZE));
        List<ReviewResponse> list = reviewService.getReviewResponseList(itemId, pageRequest);
        return ResponseEntity.ok(list);
    }

    /**
     * 특정 리뷰 제거
     *
     * @param reviewId 제거할 리뷰의 Id 값
     * @param user     로그인한 회원
     * @return 빈 응답
     */
    @Operation(summary = "특정 리뷰 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제거 성공"),
            @ApiResponse(responseCode = "400", description = "제거 실패")
    })
    @Parameter(name = "reviewId", description = "제거할 리뷰의 ID 값", example = "1")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable final long reviewId,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        reviewService.deleteReview(reviewId, user.getId());
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    /**
     * 리뷰 업데이트
     *
     * @param reviewId 업데이트할 리뷰의 Id 값
     * @param request  업데이트될 정보가 담긴 객체
     * @param user     로그인한 회원
     * @return 빈 응답
     */
    @Operation(summary = "리뷰 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 리뷰")
    })
    @Parameters(value = {
            @Parameter(name = "reviewId", description = "리뷰의 Id값", example = "1", required = true)
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> updateReview(
            @PathVariable final long reviewId,
            @RequestBody final ReviewUpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        reviewService.updateReview(reviewId, request, user.getId());
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    /**
     * 리뷰 업데이트를 위한 데이터 조회
     *
     * @param reviewId 조회할 리뷰의 Id 값
     * @param user     로그인한 회원
     * @return 리뷰 데이터
     */
    @Operation(summary = "리뷰 업데이트를 위한 데이터 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @Parameter(name = "reviewId", description = "조회할 리뷰의 Id 값", example = "1")
    @GetMapping("/{reviewId}/update")
    public ResponseEntity<ReviewUpdateResponse> updateReview(
            @PathVariable final long reviewId,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        ReviewUpdateResponse reviewUpdateResponse = reviewService.getReviewForUpdating(user.getId(), reviewId);
        return ResponseEntity.ok(reviewUpdateResponse);
    }

    /**
     * 리뷰 작성
     *
     * @param user              로그인한 사용자
     * @param multipartFileList 이미지 파일 리스트
     * @param request           생성될 리뷰의 데이터
     * @return 생성된 리뷰의 Id 값
     */
    @Operation(summary = "리뷰 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성 성공"),
            @ApiResponse(responseCode = "400", description = "리뷰 작성 실패")
    })
    @Transactional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewCreationResponse> createReview(
            @RequestPart(name = "images", required = false) final List<MultipartFile> multipartFileList,
            @RequestPart(name = "reviewCreationRequest") final ReviewCreationRequest request,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        long reviewId = reviewService.createReview(user.getId(), request);
        if (Objects.nonNull(multipartFileList) && !multipartFileList.isEmpty()) {
            reviewImageService.saveImageList(reviewId, multipartFileList);
        }
        return ResponseEntity.ok(new ReviewCreationResponse(reviewId));
    }

    @Operation(summary = "특정 회원의 리뷰 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @Parameters(value = {
            @Parameter(name = "page", description = "조회할 페이지 넘버 (기본값 0)", example = "0"),
            @Parameter(name = "size", description = "페이지당 조회할 리뷰글의 수 (기본값 10)", example = "10")
    })
    @GetMapping("/member")
    public ResponseEntity<List<ReviewResponse>> getReviewsForSpecificMember(@AuthenticationPrincipal final CustomUserDetails userDetails, final Optional<Integer> page, final Optional<Integer> size) {
        PageRequest pageable = PageRequest.of(page.orElse(0), size.orElse(PAGE_DEFAULT_SIZE));

        List<ReviewResponse> response = reviewService.getReviewForSpecificMember(userDetails.getId(), pageable);
        return ResponseEntity.ok(response);
    }
}
