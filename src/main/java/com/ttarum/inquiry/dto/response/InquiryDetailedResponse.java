package com.ttarum.inquiry.dto.response;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.dto.response.answer.InquiryAnswerResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class InquiryDetailedResponse {

    private final String title;
    private final String content;
    private final List<InquiryImageResponse> imageUrls = new ArrayList<>();
    private final InquiryAnswerResponse inquiryAnswer;

    public static InquiryDetailedResponse of(final Inquiry inquiry, InquiryAnswerResponse inquiryAnswerResponse) {
        return InquiryDetailedResponse.builder()
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .inquiryAnswer(inquiryAnswerResponse)
                .build();
    }

    public void addImageUrl(final String imageUrl) {
        imageUrls.add(new InquiryImageResponse(imageUrl));
    }
}
