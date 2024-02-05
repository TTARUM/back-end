package com.ttarum.inquiry.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.inquiry.dto.response.InquiryDetailedResponse;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
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

@Tag(name = "inquiry", description = "문의")
public interface InquiryController {

    @Operation(summary = "특정 제품의 문의글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "제품이 존재하지 않을 경우")
    })
    @Parameters(value = {
            @Parameter(name = "itemId", description = "제품의 ID", example = "1"),
            @Parameter(name = "user", hidden = true),
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "1"),
            @Parameter(name = "size", description = "한 페이지 당 조회할 문의 수 (기본 값 10개)", example = "1")
    })
    @GetMapping("/list")
    ResponseEntity<List<InquirySummaryResponse>> getInquirySummaryResponse(long itemId,
                                                                           @VerificationUser User user,
                                                                           Optional<Integer> page,
                                                                           Optional<Integer> size);

    @Operation(summary = "문의 글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @Parameters(value = {
            @Parameter(name = "inquiryId", description = "문의글의 ID", example = "1"),
            @Parameter(name = "user", hidden = true)
    })
    @GetMapping
    ResponseEntity<InquiryDetailedResponse> getInquiryDetailedResponse(long inquiryId, @VerificationUser User user);
}
