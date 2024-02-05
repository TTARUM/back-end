package com.ttarum.inquiry.service;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.dto.response.InquiryDetailedResponse;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import com.ttarum.inquiry.exception.InquiryException;
import com.ttarum.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public List<InquirySummaryResponse> getInquirySummaryResponseList(final long itemId, final long memberId, final Pageable pageable) {
        return inquiryRepository.findInquirySummaryByItemIdAndMemberId(itemId, memberId, pageable);
    }

    public List<InquirySummaryResponse> getInquirySummaryResponseList(final long itemId, final Pageable pageable) {
        return inquiryRepository.findInquirySummaryByItemId(itemId, pageable);
    }

    public InquiryDetailedResponse getInquiryDetailedResponse(final long inquiryId, final long memberId) {
        Inquiry inquiry = getInquiryById(inquiryId);
        validateAccessToInquiry(memberId, inquiry);
        InquiryDetailedResponse response = InquiryDetailedResponse.of(inquiry);

        List<String> imageUrls = inquiryRepository.findInquiryImageByInquiryId(inquiryId);
        for (String imageUrl : imageUrls) {
            response.addImageUrl(imageUrl);
        }
        return response;
    }

    private void validateAccessToInquiry(final long memberId, final Inquiry inquiry) {
        if (Boolean.TRUE.equals(inquiry.getIsSecret()) && !inquiry.getMember().getId().equals(memberId)) {
            throw new InquiryException("해당 문의글을 열람할 권한이 없습니다.");
        }
    }

    private Inquiry getInquiryById(final long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new InquiryException("해당 문의 글을 찾을 수 없습니다."));
    }

    public InquiryDetailedResponse getInquiryDetailedResponse(final long inquiryId) {
        Inquiry inquiry = getInquiryById(inquiryId);
        validateAccessToInquiry(inquiry);
        InquiryDetailedResponse response = InquiryDetailedResponse.of(inquiry);

        List<String> imageUrls = inquiryRepository.findInquiryImageByInquiryId(inquiryId);
        for (String imageUrl : imageUrls) {
            response.addImageUrl(imageUrl);
        }
        return response;
    }

    private void validateAccessToInquiry(final Inquiry inquiry) {
        if (Boolean.TRUE.equals(inquiry.getIsSecret())) {
            throw new InquiryException("해당 문의글을 열람할 권한이 없습니다.");
        }
    }
}
