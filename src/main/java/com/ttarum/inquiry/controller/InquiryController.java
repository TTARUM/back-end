package com.ttarum.inquiry.controller;

import com.ttarum.auth.domain.UserDetail;
import com.ttarum.inquiry.dto.request.InquiryCreationRequest;
import com.ttarum.inquiry.dto.response.InquiryCreationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "inquiry", description = "문의")
public interface InquiryController {

    @Transactional
    @PostMapping
    ResponseEntity<InquiryCreationResponse> postInquiry(@RequestPart(name = "inquiryRequest") InquiryCreationRequest request,
                                                        @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                                        @AuthenticationPrincipal UserDetail user);
}
