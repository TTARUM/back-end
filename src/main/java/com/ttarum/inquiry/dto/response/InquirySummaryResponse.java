package com.ttarum.inquiry.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class InquirySummaryResponse {

    private final String title;
    private final boolean isThisOwnInquiry;
    private final boolean hasAnswer;
    private final String memberName;
    private final Instant createdAt;
    // fixme 문의 답변 형식 알 수 없음
    private final String content;
}