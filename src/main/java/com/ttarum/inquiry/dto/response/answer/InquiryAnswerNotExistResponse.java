package com.ttarum.inquiry.dto.response.answer;

public class InquiryAnswerNotExistResponse extends InquiryAnswerResponse {

    public static final String NOT_EXISTS_MESSAGE = "답변이 존재하지 않습니다.";

    public InquiryAnswerNotExistResponse(final String content) {
        super(content);
    }

    public InquiryAnswerNotExistResponse() {
        super(NOT_EXISTS_MESSAGE);
    }
}
