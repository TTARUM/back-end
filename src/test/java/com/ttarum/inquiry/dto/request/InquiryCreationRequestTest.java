package com.ttarum.inquiry.dto.request;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.exception.InquiryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InquiryCreationRequestTest {

    @Test
    @DisplayName("Inquiry Entity로 변환할 수 있다.")
    void transferToInquiry() {
        // given
        InquiryCreationRequest inquiryCreationRequest = InquiryCreationRequest.builder()
                .title("title")
                .content("content")
                .isSecret(false)
                .build();

        // when
        Inquiry inquiry = inquiryCreationRequest.transferToInquiry();

        // then
        assertThat(inquiry.getTitle()).isEqualTo(inquiryCreationRequest.getTitle());
        assertThat(inquiry.getContent()).isEqualTo(inquiryCreationRequest.getContent());
        assertThat(inquiry.getIsSecret()).isEqualTo(inquiryCreationRequest.isSecret());
    }

    @Test
    @DisplayName("제목이 널 값이면 예외가 발생한다.")
    void transferToInquiryFailureByNullTitle() {
        // given
        InquiryCreationRequest inquiryCreationRequest = InquiryCreationRequest.builder()
                .title(null)
                .content("content")
                .isSecret(false)
                .build();

        // when

        // then
        assertThatThrownBy(inquiryCreationRequest::transferToInquiry)
                .isInstanceOf(InquiryException.class)
                .hasMessage("제목이 비어있습니다.");
    }

    @Test
    @DisplayName("제목이 비어있으면 예외가 발생한다.")
    void transferToInquiryFailureByEmptyTitle() {
        // given
        InquiryCreationRequest inquiryCreationRequest = InquiryCreationRequest.builder()
                .title("")
                .content("content")
                .isSecret(false)
                .build();

        // when

        // then
        assertThatThrownBy(inquiryCreationRequest::transferToInquiry)
                .isInstanceOf(InquiryException.class)
                .hasMessage("제목이 비어있습니다.");
    }

    @Test
    @DisplayName("내용이 널 값이면 예외가 발생한다.")
    void transferToInquiryFailureByNullContent() {
        // given
        InquiryCreationRequest inquiryCreationRequest = InquiryCreationRequest.builder()
                .title("title")
                .content(null)
                .isSecret(false)
                .build();

        // when

        // then
        assertThatThrownBy(inquiryCreationRequest::transferToInquiry)
                .isInstanceOf(InquiryException.class)
                .hasMessage("내용이 비어있습니다.");
    }

    @Test
    @DisplayName("내용이 비어있으면 예외가 발생한다.")
    void transferToInquiryFailureByEmptyContent() {
        // given
        InquiryCreationRequest inquiryCreationRequest = InquiryCreationRequest.builder()
                .title("title")
                .content("")
                .isSecret(false)
                .build();

        // when

        // then
        assertThatThrownBy(inquiryCreationRequest::transferToInquiry)
                .isInstanceOf(InquiryException.class)
                .hasMessage("내용이 비어있습니다.");
    }
}