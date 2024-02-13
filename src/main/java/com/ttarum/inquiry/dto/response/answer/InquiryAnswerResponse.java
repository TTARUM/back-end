package com.ttarum.inquiry.dto.response.answer;

import com.ttarum.inquiry.domain.InquiryAnswer;
import lombok.Getter;

@Getter
public abstract class InquiryAnswerResponse {

    private final String content;

    protected InquiryAnswerResponse(final String content) {
        this.content = content;
    }

    public static InquiryAnswerResponse of(final InquiryAnswer inquiryAnswer) {
        return InquiryAnswerExistResponse.builder()
                .content(inquiryAnswer.getContent())
                .build();
    }
}