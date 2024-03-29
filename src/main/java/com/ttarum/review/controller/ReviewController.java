package com.ttarum.review.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.review.dto.request.ReviewCreationRequest;
import com.ttarum.review.dto.request.ReviewUpdateRequest;
import com.ttarum.review.dto.response.ReviewCreationResponse;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.dto.response.ReviewUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Tag(name = "review", description = "리뷰")
public interface ReviewController {

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
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "1"),
            @Parameter(name = "size", description = "한 페이지당 리뷰 수 (기본 값 10개)", example = "5")

    })
    @GetMapping
    ResponseEntity<List<ReviewResponse>> getReviewResponseList(@RequestParam long itemId,
                                                               Optional<Integer> page,
                                                               Optional<Integer> size);

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
    @DeleteMapping
    ResponseEntity<Void> deleteReview(@PathVariable long reviewId, @AuthenticationPrincipal CustomUserDetails user);

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
    @PutMapping
    ResponseEntity<Void> updateReview(@PathVariable long reviewId, @RequestBody ReviewUpdateRequest request, @AuthenticationPrincipal CustomUserDetails user);

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
    @GetMapping
    ResponseEntity<ReviewUpdateResponse> updateReview(@PathVariable long reviewId, @AuthenticationPrincipal CustomUserDetails user);

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ReviewCreationResponse> createReview(@AuthenticationPrincipal CustomUserDetails user, @RequestPart(name = "images") List<MultipartFile> multipartFileList, @RequestPart(name = "reviewCreationRequest") ReviewCreationRequest request);

}
