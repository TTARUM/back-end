package com.ttarum.review.controller;

import com.ttarum.review.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Tag(name = "review", description = "리뷰")
public interface ReviewController {

    /**
     * 특정 제품에 대한 리뷰 조회
     *
     * @param itemId 특정 제품의 ID
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
    ResponseEntity<List<ReviewResponse>> getReviewResponseList(final Long itemId,
                                                               final Optional<Integer> page,
                                                               final Optional<Integer> size);

}
