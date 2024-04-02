package com.ttarum.inquiry.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InquirySummaryResponseTest {

    @Test
    @DisplayName("이름이 한 글자인 경우")
    void concealOneLetterName() {
        // given && when
        InquirySummaryResponse response = InquirySummaryResponse.builder()
                .memberName("a")
                .build();

        // then
        assertThat(response.getMemberName()).isEqualTo("*");
    }

    @Test
    @DisplayName("이름이 두 글자인 경우")
    void concealTwoLettersName() {
        // given && when
        InquirySummaryResponse response = InquirySummaryResponse.builder()
                .memberName("ab")
                .build();

        // then
        assertThat(response.getMemberName()).isEqualTo("a*");
    }

    @Test
    @DisplayName("이름이 세 글자인 경우")
    void concealThreeLettersName() {
        // given && when
        InquirySummaryResponse response = InquirySummaryResponse.builder()
                .memberName("two")
                .build();

        // then
        assertThat(response.getMemberName()).isEqualTo("t*o");
    }

    @Test
    @DisplayName("자신의 문의글인 경우")
    void setOriginalTitle() {
        // given && when
        InquirySummaryResponse response = InquirySummaryResponse.builder()
                .title("title")
                .memberName("memberName")
                .isThisOwnInquiry(true)
                .build();

        // then
        assertThat(response.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("자신의 문의글이 아니며, 비밀글이 아닌 경우")
    void setOriginalTitleByNotSecretInquiry() {
        // given && when
        InquirySummaryResponse response = InquirySummaryResponse.builder()
                .title("title")
                .memberName("memberName")
                .isThisOwnInquiry(false)
                .isSecretInquiry(false)
                .build();

        // then
        assertThat(response.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("자신의 문의글이 아니며, 비밀글인 경우")
    void setSecretTitleBySecretInquiry() {
        // given && when
        InquirySummaryResponse response = InquirySummaryResponse.builder()
                .title("title")
                .memberName("memberName")
                .isThisOwnInquiry(false)
                .isSecretInquiry(true)
                .build();

        // then
        assertThat(response.getTitle()).isEqualTo(InquirySummaryResponse.SECRET_INQUIRY_TITLE);
    }
}