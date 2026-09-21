package com.ttarum.inquiry.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
@Schema(description = "문의글 이미지 DTO")
public class InquiryImageResponse {

    @Schema(description = "이미지 URL", example = "ttarum.inquiry_image.url")
    private final String imageUrl;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InquiryImageResponse that = (InquiryImageResponse) o;
        return Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl);
    }
}
