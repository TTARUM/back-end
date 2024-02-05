package com.ttarum.inquiry.dto.response;

import com.ttarum.inquiry.domain.Inquiry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class InquiryDetailedResponse {

    private static final String DEFAULT_ANSWER_CONTENT = "문의 답변 예 입니다.";

    private final String title;
    private final List<InquiryImageResponse> imageUrls = new ArrayList<>();
    private final String answerContent = DEFAULT_ANSWER_CONTENT; // 관리자 페이지가 없으므로 예시 답변으로 설정

    public static InquiryDetailedResponse of(final Inquiry inquiry) {
        return InquiryDetailedResponse.builder()
                .title(inquiry.getTitle())
                .build();
    }

    public void addImageUrl(final String imageUrl) {
        imageUrls.add(new InquiryImageResponse(imageUrl));
    }
}
