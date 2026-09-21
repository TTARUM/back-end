package com.ttarum.inquiry.dto.request;

import com.ttarum.inquiry.domain.Inquiry;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "문의글 생성 DTO")
public class InquiryCreationRequest {

    @Schema(description = "문의글 제목", example = "와인에서 뭔가가 씹혀요..")
    private final String title;

    @Schema(description = "문의글 내용", example = "와인에 건더기가 존재하나요..?")
    private final String content;

    @Schema(description = "비밀글 여부", example = "true")
    private final boolean isSecret;

    @Schema(description = "문의글이 작성될 제품의 Id 값", example = "1")
    private final long itemId;

    public Inquiry toInquiryEntity() {
        Inquiry inquiry = Inquiry.builder()
                .title(this.title)
                .content(this.content)
                .isSecret(isSecret)
                .build();
        inquiry.validate();
        return inquiry;
    }
}
