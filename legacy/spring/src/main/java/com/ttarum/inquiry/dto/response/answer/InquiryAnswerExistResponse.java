package com.ttarum.inquiry.dto.response.answer;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InquiryAnswerExistResponse extends InquiryAnswerResponse {

    @Builder
    protected InquiryAnswerExistResponse(final String content) {
        super(content);
    }
}
