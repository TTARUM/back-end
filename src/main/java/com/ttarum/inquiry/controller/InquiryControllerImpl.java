package com.ttarum.inquiry.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.inquiry.dto.response.InquiryDetailedResponse;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import com.ttarum.inquiry.service.InquiryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/inquiries")
public class InquiryControllerImpl implements InquiryController {

    private static final int INQUIRY_DEFAULT_SIZE_PER_PAGE = 10;
    private final InquiryService inquiryService;

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
}
