package com.ttarum.inquiry.dto.request;

import com.ttarum.inquiry.domain.Inquiry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InquiryCreationRequest {

    private final String title;
    private final String content;
    private final boolean isSecret;
    private final long itemId;

    public Inquiry transferToInquiry() {
        Inquiry inquiry = Inquiry.builder()
                .title(this.title)
                .content(this.content)
                .isSecret(isSecret)
                .build();
        inquiry.validate();
        return inquiry;
    }
}
