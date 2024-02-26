package com.ttarum.inquiry.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "문의글 생성 후 응답 DTO")
public class InquiryCreationResponse {

    @Schema(description = "생성된 문의글의 Id 값", example = "1")
    private final long inquiryId;
}
