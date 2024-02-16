package com.ttarum.inquiry.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
                                                                                  @VerificationUser final User user,
                                                                                  Optional<Integer> page,
                                                                                  Optional<Integer> size) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(INQUIRY_DEFAULT_SIZE_PER_PAGE));
        List<InquirySummaryResponse> list;
        if (user.isLoggedIn()) {
            list = inquiryService.getInquirySummaryResponseList(itemId, user.getId(), pageRequest);
        } else {
            list = inquiryService.getInquirySummaryResponseList(itemId, pageRequest);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping
    @Override
    public ResponseEntity<InquiryDetailedResponse> getInquiryDetailedResponse(final long inquiryId, final User user) {
        InquiryDetailedResponse response;
        if (user.isLoggedIn()) {
            response = inquiryService.getInquiryDetailedResponse(inquiryId, user.getId());
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
            for (MultipartFile image : images) {
                inquiryImageService.saveImage(inquiryId, image);
            }
        }

        return ResponseEntity.ok(new InquiryCreationResponse(inquiryId));
    }
}
