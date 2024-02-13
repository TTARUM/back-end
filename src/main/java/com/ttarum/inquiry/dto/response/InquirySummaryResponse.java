package com.ttarum.inquiry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class InquirySummaryResponse {

    private static final String SECRET_INQUIRY_TITLE = "비밀글입니다.";

    private final Long id;
    private String title;
    private final boolean isSecretInquiry;
    private final boolean isThisOwnInquiry;
    private final boolean hasAnswer;
    private final String memberName; // O 백엔드에서 이름 모자이크
    private final Instant createdAt;

    public InquirySummaryResponse(final Long id,
                                  final String title,
                                  final boolean isSecretInquiry,
                                  final boolean isThisOwnInquiry,
                                  final boolean hasAnswer,
                                  final String memberName,
                                  final Instant createdAt) {
        this.id = id;
        this.isSecretInquiry = isSecretInquiry;
        this.isThisOwnInquiry = isThisOwnInquiry;
        this.hasAnswer = hasAnswer;
        this.memberName = memberName;
        this.createdAt = createdAt;
        if (isThisOwnInquiry || !isSecretInquiry) {
            this.title = title;
        }
    }
}