package com.ttarum.inquiry.dto.response;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.dto.response.answer.InquiryAnswerResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "문의글 상세보기 DTO")
public class InquiryDetailedResponse {

    @Schema(description = "문의글 제목", example = "와인에 건더기가 있나요?")
    private final String title;

    @Schema(description = "문의글 내용", example = "와인을 마셨는데 뭔가가 씹혀요. 와인에 건더기가 존재하나요?")
    private final String content;

    @Schema(description = "문의글 이미지 리스트")
    private final List<InquiryImageResponse> imageUrls = new ArrayList<>();

    @Schema(description = "문의글 답변 DTO / 인증, 인가되지 않은 사용자에게는 '답변이 존재하지 않습니다.'로 응답됩니다.")
    private final InquiryAnswerResponse inquiryAnswer;

    public static InquiryDetailedResponse of(final Inquiry inquiry, InquiryAnswerResponse inquiryAnswerResponse) {
        return InquiryDetailedResponse.builder()
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .inquiryAnswer(inquiryAnswerResponse)
                .build();
    }

    public void addImageUrl(final String imageUrl) {
        imageUrls.add(new InquiryImageResponse(imageUrl));
    }
}
