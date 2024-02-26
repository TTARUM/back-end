package com.ttarum.inquiry.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.inquiry.dto.request.InquiryCreationRequest;
import com.ttarum.inquiry.dto.response.InquiryCreationResponse;
import com.ttarum.inquiry.dto.response.InquiryDetailedResponse;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Tag(name = "inquiry", description = "문의")
public interface InquiryController {

    /**
     * 특정 제품의 문의글 조회
     *
     * @param itemId 특정 제품의 Id 값
     * @param user   로그인한 사용자 여부를 확인하기 위한 객체
     * @param page   페이지 넘버
     * @param size   페이지 당 조회할 문의글 수
     * @return 조회된 문의글 리스트
     */
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

    /**
     * 문의글 조회
     *
     * @param inquiryId 조회할 문의글의 Id 값
     * @param user      로그인한 사용자 여부를 확인하기 위한 객체
     * @return 문의글
     */
    @Operation(summary = "문의글 조회")
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

    /**
     * 문의글 등록 메서드
     *
     * @param request 등록할 문의글의 데이터가 담긴 객체
     * @param images  이미지 파일들
     * @param user    로그인한 회원
     * @return 등록된 문의글의 Id 값
     */
    @Operation(summary = "문의글 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의글 등록 성공"),
            @ApiResponse(responseCode = "400", description = "문의글 등록 실패")
    })
    @Transactional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InquiryCreationResponse> postInquiry(@RequestPart(name = "inquiryRequest") InquiryCreationRequest request,
                                                        @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                                        @AuthenticationPrincipal CustomUserDetails user);
}
