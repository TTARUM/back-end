package com.ttarum.inquiry.dto.response.answer;

import com.ttarum.inquiry.domain.InquiryAnswer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "문의 답변 DTO")
public abstract class InquiryAnswerResponse {

    @Schema(description = "답변 내용", example = "답변 내용 예시입니다.")
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