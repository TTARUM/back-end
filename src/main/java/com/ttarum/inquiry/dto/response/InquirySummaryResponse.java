package com.ttarum.inquiry.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@Schema(description = "문의글 미리보기 DTO")
public class InquirySummaryResponse {

    private static final String SECRET_INQUIRY_TITLE = "비밀글입니다.";

    @Schema(description = "문의글 Id 값", example = "1")
    private final Long id;

    @Schema(description = "문의글 제목 / 인증, 인가되지 않은 사용자에게는 '비밀글입니다.'의 제목으로 응답됩니다.", example = "와인에서 뭔가가 씹혀요..")
    private String title;

    @Schema(description = "비밀글 여부", example = "true")
    private final boolean isSecretInquiry;

    @Schema(description = "자신의 문의글 여부", example = "true")
    private final boolean isThisOwnInquiry;

    @Schema(description = "문의글 답변 존재 여부", example = "true")
    private final boolean hasAnswer;

    @Schema(description = "회원의 이름", example = "홍*동")
    private final String memberName; // O 백엔드에서 이름 모자이크

    @Schema(description = "문의글 생성일", example = "2024-02-23T04:32:49.584863Z")
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