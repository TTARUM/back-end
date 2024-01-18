package com.ttarum.review.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.review.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "review", description = "리뷰")
public interface ReviewController {

    @Operation(summary = "특정 제품에 대한 리뷰 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "유효하지 않은 제품 조회")
    })
    @Parameter(name = "itemId", description = "제품의 PK 값", example ="1")
    @GetMapping
    ResponseEntity<List<ReviewResponse>> getReviewResponseList(final Long itemId, @VerificationUser final User user);
}
