package com.ttarum.inquiry.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.LoggedInUser;
import com.ttarum.inquiry.dto.response.InquiryDetailedResponse;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import com.ttarum.inquiry.service.InquiryService;
import com.ttarum.inquiry.dto.request.InquiryCreationRequest;
import com.ttarum.inquiry.dto.response.InquiryCreationResponse;
import com.ttarum.inquiry.service.InquiryImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryControllerImpl implements InquiryController {

    private static final int INQUIRY_DEFAULT_SIZE_PER_PAGE = 10;
    private final InquiryService inquiryService;
    private final InquiryImageService inquiryImageService;

    @GetMapping("/list")
    @Override
    public ResponseEntity<List<InquirySummaryResponse>> getInquirySummaryResponse(final long itemId,
                                                                                  @VerificationUser final Optional<LoggedInUser> user,
                                                                                  Optional<Integer> page,
                                                                                  Optional<Integer> size) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(INQUIRY_DEFAULT_SIZE_PER_PAGE));
        List<InquirySummaryResponse> list;
        if (user.isPresent()) {
            list = inquiryService.getInquirySummaryResponseList(itemId, user.get().getId(), pageRequest);
        } else {
            list = inquiryService.getInquirySummaryResponseList(itemId, pageRequest);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{inquiryId}")
    @Override
    public ResponseEntity<InquiryDetailedResponse> getInquiryDetailedResponse(@PathVariable final long inquiryId, @VerificationUser final Optional<LoggedInUser> user) {
        InquiryDetailedResponse response;
        if (user.isPresent()) {
            response = inquiryService.getInquiryDetailedResponse(inquiryId, user.get().getId());
        } else {
            response = inquiryService.getInquiryDetailedResponse(inquiryId);
        }
        return ResponseEntity.ok(response);
    }

    @Transactional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<InquiryCreationResponse> postInquiry(@RequestPart(name = "inquiryRequest") final InquiryCreationRequest request,
                                                               @RequestPart(name = "images", required = false) final List<MultipartFile> images,
                                                               @AuthenticationPrincipal final CustomUserDetails user) {
        long inquiryId = inquiryService.postInquiryArticle(request, user.getId());

        if (Objects.nonNull(images)) {
            inquiryImageService.saveImageList(inquiryId, images);
        }

        return ResponseEntity.ok(new InquiryCreationResponse(inquiryId));
    }
}
