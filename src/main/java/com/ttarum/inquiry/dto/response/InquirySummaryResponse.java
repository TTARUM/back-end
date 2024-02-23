package com.ttarum.inquiry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class InquirySummaryResponse {

    public static final String SECRET_INQUIRY_TITLE = "비밀글입니다.";

    private final Long id;
    private final String title;
    private final boolean isSecretInquiry;
    private final boolean isThisOwnInquiry;
    private final boolean hasAnswer;
    private final String memberName;
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
        this.memberName = concealName(memberName);
        this.createdAt = createdAt;
        if (isThisOwnInquiry || !isSecretInquiry) {
            this.title = title;
        } else {
            this.title = SECRET_INQUIRY_TITLE;
        }
    }

    private String concealName(final String memberName) {
        if (memberName.length() < 2) {
            return "*";
        }
        if (memberName.length() == 2) {
            return memberName.charAt(0) + "*";
        }
        return memberName.charAt(0) +
                "*".repeat(memberName.length() - 2) +
                memberName.charAt(memberName.length() - 1);
    }
}