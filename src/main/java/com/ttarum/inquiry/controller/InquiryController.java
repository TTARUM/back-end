package com.ttarum.inquiry.controller;

import com.ttarum.auth.domain.UserDetail;
import com.ttarum.inquiry.dto.request.InquiryCreationRequest;
import com.ttarum.inquiry.dto.response.InquiryCreationResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "inquiry", description = "문의")
public interface InquiryController {

    @Operation(summary = "문의글 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의글 등록 성공"),
            @ApiResponse(responseCode = "400", description = "문의글 등록 실패")
    })
    @Parameters(value = {
            @Parameter(name = "inquiryRequest", description = "등록할 문의글", required = true, example = """
                    {
                        "title": "문의글 제목",
                        "content": "문의글 내용",
                        "isSecret": false,
                        "itemId": 1
                    }
                    """),
            @Parameter(name = "images", description = "이미지 파일들"),
            @Parameter(name = "user", hidden = true)
    })
    @Transactional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InquiryCreationResponse> postInquiry(@RequestPart(name = "inquiryRequest") InquiryCreationRequest request,
                                                        @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                                        @AuthenticationPrincipal UserDetail user);
}
