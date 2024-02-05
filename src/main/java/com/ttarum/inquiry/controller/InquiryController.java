package com.ttarum.inquiry.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.inquiry.dto.response.InquiryDetailedResponse;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Tag(name = "inquiry", description = "문의")
public interface InquiryController {

    @GetMapping("/list")
    ResponseEntity<List<InquirySummaryResponse>> getInquirySummaryResponse(long itemId,
                                                                           @VerificationUser User user,
                                                                           Optional<Integer> page,
                                                                           Optional<Integer> size);

    @GetMapping
    ResponseEntity<InquiryDetailedResponse> getInquiryDetailedResponse(long inquiryId, @VerificationUser User user);
}
