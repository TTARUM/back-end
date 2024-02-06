package com.ttarum.inquiry.controller;

import com.ttarum.auth.domain.UserDetail;
import com.ttarum.inquiry.dto.request.InquiryCreationRequest;
import com.ttarum.inquiry.dto.response.InquiryCreationResponse;
import com.ttarum.inquiry.service.InquiryImageService;
import com.ttarum.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryControllerImpl implements InquiryController {

    private final InquiryService inquiryService;
    private final InquiryImageService inquiryImageService;

    @Transactional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<InquiryCreationResponse> postInquiry(@RequestPart(name = "inquiryRequest") final InquiryCreationRequest request,
                                                               @RequestPart(name = "images", required = false) final List<MultipartFile> images,
                                                               @AuthenticationPrincipal final UserDetail user) {
        long inquiryId = inquiryService.postInquiry(request, user.getId());

        if (Objects.nonNull(images)) {
            for (MultipartFile image : images) {
                inquiryImageService.saveImage(inquiryId, image);
            }
        }

        return ResponseEntity.ok(new InquiryCreationResponse(inquiryId));
    }
}
