package com.ttarum.inquiry.service;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.domain.InquiryAnswer;
import com.ttarum.inquiry.dto.response.InquiryDetailedResponse;
import com.ttarum.inquiry.dto.response.InquiryImageResponse;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import com.ttarum.inquiry.dto.response.answer.InquiryAnswerExistResponse;
import com.ttarum.inquiry.dto.response.answer.InquiryAnswerNotExistResponse;
import com.ttarum.inquiry.exception.InquiryException;
import com.ttarum.inquiry.repository.InquiryRepository;
import com.ttarum.item.domain.Item;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InquiryServiceTest {

    @Mock
    InquiryRepository inquiryRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    InquiryService inquiryService;

    @Test
    @DisplayName("로그인한 사용자는 특정 제품의 문의글을 조회할 수 있다. - 로그인한 사용자")
    void getInquirySummaryResponseListWithLoggedInMember() {
        // given
        long inquiryId = 1L;
        long itemId = 1L;
        long memberId = 1L;
        Instant instant = Instant.now();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<InquirySummaryResponse> inquirySummaryResponseList = List.of(new InquirySummaryResponse(inquiryId, "title", false, false, false, "memberName", instant));

        // when
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(Item.builder().id(itemId).build()));
        when(inquiryRepository.findInquirySummaryByItemIdAndMemberId(itemId, memberId, pageRequest)).thenReturn(inquirySummaryResponseList);

        List<InquirySummaryResponse> result = inquiryService.getInquirySummaryResponseList(itemId, memberId, pageRequest);
        InquirySummaryResponse response = result.get(0);

        // then
        assertThat(result.size()).isOne();
        assertThat(response.getId()).isEqualTo(inquiryId);
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getCreatedAt()).isEqualTo(instant);
        assertThat(response.getMemberName()).isEqualTo("memberName");
    }

    @Test
    @DisplayName("존재하지 않는 제품의 문의글을 조회할 경우 예외가 발생한다. - 로그인한 사용자")
    void getInquirySummaryResponseListWithLoggedInMemberFailureByInvalidItemId() {
        // given
        long itemId = 1L;
        long memberId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> inquiryService.getInquirySummaryResponseList(itemId, memberId, pageRequest))
                .isInstanceOf(InquiryException.class)
                .hasMessage("제품이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("특정 제품의 문의글을 조회할 수 있다. - 로그인하지 않은 사용자")
    void getInquirySummaryResponseList() {
        // given
        long inquiryId = 1L;
        long itemId = 1L;
        Instant instant = Instant.now();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<InquirySummaryResponse> inquirySummaryResponseList = List.of(new InquirySummaryResponse(inquiryId, "title", false, false, false, "memberName", instant));

        // when
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(Item.builder().id(itemId).build()));
        when(inquiryRepository.findInquirySummaryByItemId(itemId, pageRequest)).thenReturn(inquirySummaryResponseList);

        List<InquirySummaryResponse> result = inquiryService.getInquirySummaryResponseList(itemId, pageRequest);
        InquirySummaryResponse response = result.get(0);

        // then
        assertThat(result.size()).isOne();
        assertThat(response.getId()).isEqualTo(inquiryId);
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getCreatedAt()).isEqualTo(instant);
        assertThat(response.getMemberName()).isEqualTo("memberName");
    }

    @Test
    @DisplayName("존재하지 않는 제품의 문의글을 조회할 경우 예외가 발생한다. - 로그인하지 않은 사용자")
    void getInquirySummaryResponseListFailureByInvalidItemId() {
        // given
        long itemId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> inquiryService.getInquirySummaryResponseList(itemId, pageRequest))
                .isInstanceOf(InquiryException.class)
                .hasMessage("제품이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("특정 문의글을 조회할 수 있다. - 로그인한 사용자, 답변이 존재하지 않는 경우")
    void getInquiryDetailedResponseWithNotExistsAnswerAndLoggedInMember() {
        // given
        long inquiryId = 1L;
        long memberId = 1L;
        Inquiry inquiry = Inquiry.builder()
                .id(inquiryId)
                .title("title")
                .content("content")
                .existAnswer(false)
                .isSecret(false)
                .build();
        List<String> imageUrls = List.of("imageUrl1", "imageUrl2");

        // when
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(inquiry));
        when(inquiryRepository.findInquiryImageByInquiryId(inquiryId)).thenReturn(imageUrls);
        InquiryDetailedResponse inquiryDetailedResponse = inquiryService.getInquiryDetailedResponse(inquiryId, memberId);

        // then
        assertThat(inquiryDetailedResponse.getTitle()).isEqualTo(inquiry.getTitle());
        assertThat(inquiryDetailedResponse.getContent()).isEqualTo(inquiry.getContent());
        assertThat(inquiryDetailedResponse.getImageUrls())
                .containsExactlyInAnyOrder(
                        new InquiryImageResponse("imageUrl1"),
                        new InquiryImageResponse("imageUrl2")
                );
        assertThat(inquiryDetailedResponse.getInquiryAnswer()).isInstanceOf(InquiryAnswerNotExistResponse.class);
    }

    @Test
    @DisplayName("특정 문의글을 조회할 수 있다. - 로그인한 사용자, 답변이 존재하는 경우")
    void getInquiryDetailedResponseWithExistsAnswerAndLoggedInMember() {
        // given
        long inquiryId = 1L;
        long memberId = 1L;
        Inquiry inquiry = Inquiry.builder()
                .id(inquiryId)
                .title("title")
                .content("content")
                .existAnswer(true)
                .isSecret(false)
                .build();
        InquiryAnswer inquiryAnswer = InquiryAnswer.builder()
                .inquiry(inquiry)
                .content("content")
                .build();
        List<String> imageUrls = List.of("imageUrl1", "imageUrl2");

        // when
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(inquiry));
        when(inquiryRepository.findInquiryImageByInquiryId(inquiryId)).thenReturn(imageUrls);
        when(inquiryRepository.findAnswerByInquiryId(inquiryId)).thenReturn(Optional.of(inquiryAnswer));
        InquiryDetailedResponse inquiryDetailedResponse = inquiryService.getInquiryDetailedResponse(inquiryId, memberId);

        // then
        assertThat(inquiryDetailedResponse.getTitle()).isEqualTo(inquiry.getTitle());
        assertThat(inquiryDetailedResponse.getContent()).isEqualTo(inquiry.getContent());
        assertThat(inquiryDetailedResponse.getImageUrls())
                .containsExactlyInAnyOrder(
                        new InquiryImageResponse("imageUrl1"),
                        new InquiryImageResponse("imageUrl2")
                );
        assertThat(inquiryDetailedResponse.getInquiryAnswer()).isInstanceOf(InquiryAnswerExistResponse.class);
    }

    @Test
    @DisplayName("특정 문의글을 조회할 수 있다. - 로그인하지 않은 사용자, 답변이 존재하는 경우")
    void getInquiryDetailedResponseWithExistsAnswer() {
        // given
        long inquiryId = 1L;
        Inquiry inquiry = Inquiry.builder()
                .id(inquiryId)
                .title("title")
                .content("content")
                .existAnswer(true)
                .isSecret(false)
                .build();
        InquiryAnswer inquiryAnswer = InquiryAnswer.builder()
                .inquiry(inquiry)
                .content("content")
                .build();
        List<String> imageUrls = List.of("imageUrl1", "imageUrl2");

        // when
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(inquiry));
        when(inquiryRepository.findInquiryImageByInquiryId(inquiryId)).thenReturn(imageUrls);
        when(inquiryRepository.findAnswerByInquiryId(inquiryId)).thenReturn(Optional.of(inquiryAnswer));
        InquiryDetailedResponse inquiryDetailedResponse = inquiryService.getInquiryDetailedResponse(inquiryId);

        // then
        assertThat(inquiryDetailedResponse.getTitle()).isEqualTo(inquiry.getTitle());
        assertThat(inquiryDetailedResponse.getContent()).isEqualTo(inquiry.getContent());
        assertThat(inquiryDetailedResponse.getImageUrls())
                .containsExactlyInAnyOrder(
                        new InquiryImageResponse("imageUrl1"),
                        new InquiryImageResponse("imageUrl2")
                );
        assertThat(inquiryDetailedResponse.getInquiryAnswer()).isInstanceOf(InquiryAnswerExistResponse.class);
    }

    @Test
    @DisplayName("특정 문의글을 조회할 수 있다. - 로그인하지 않은 사용자, 답변이 존재하지 않는 경우")
    void getInquiryDetailedResponseWithNotExistsAnswer() {
        // given
        long inquiryId = 1L;
        Inquiry inquiry = Inquiry.builder()
                .id(inquiryId)
                .title("title")
                .content("content")
                .existAnswer(false)
                .isSecret(false)
                .build();
        List<String> imageUrls = List.of("imageUrl1", "imageUrl2");

        // when
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(inquiry));
        when(inquiryRepository.findInquiryImageByInquiryId(inquiryId)).thenReturn(imageUrls);
        InquiryDetailedResponse inquiryDetailedResponse = inquiryService.getInquiryDetailedResponse(inquiryId);

        // then
        assertThat(inquiryDetailedResponse.getTitle()).isEqualTo(inquiry.getTitle());
        assertThat(inquiryDetailedResponse.getContent()).isEqualTo(inquiry.getContent());
        assertThat(inquiryDetailedResponse.getImageUrls())
                .containsExactlyInAnyOrder(
                        new InquiryImageResponse("imageUrl1"),
                        new InquiryImageResponse("imageUrl2")
                );
        assertThat(inquiryDetailedResponse.getInquiryAnswer()).isInstanceOf(InquiryAnswerNotExistResponse.class);
    }

    @Test
    @DisplayName("존재하지 않는 문의글을 조회하는 경우 예외가 발생한다. - 로그인한 경우")
    void getInquiryDetailedResponseFailureByInvalidInquiryIdWithLoggedInMember() {
        // given
        long inquiryId = 1L;
        long memberId = 1L;

        // when
        when(inquiryRepository.findById(inquiryId)).thenThrow(new InquiryException("해당 문의 글을 찾을 수 없습니다."));

        // then
        assertThatThrownBy(() -> inquiryService.getInquiryDetailedResponse(inquiryId, memberId))
                .isInstanceOf(InquiryException.class)
                .hasMessage("해당 문의 글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 문의글을 조회하는 경우 예외가 발생한다. - 로그인하지 않은 경우")
    void getInquiryDetailedResponseFailureByInvalidInquiryId() {
        // given
        long inquiryId = 1L;

        // when
        when(inquiryRepository.findById(inquiryId)).thenThrow(new InquiryException("해당 문의 글을 찾을 수 없습니다."));

        // then
        assertThatThrownBy(() -> inquiryService.getInquiryDetailedResponse(inquiryId))
                .isInstanceOf(InquiryException.class)
                .hasMessage("해당 문의 글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("권한이 없는 문의글을 조회하는 경우 예외가 발생한다. - 비밀글의 경우, 로그인하지 않은 사용자")
    void getInquiryDetailedResponseFailureBySecretInquiry() {
        // given
        long inquiryId = 1L;
        Inquiry inquiry = Inquiry.builder()
                .id(inquiryId)
                .title("title")
                .content("content")
                .existAnswer(false)
                .isSecret(true)
                .build();

        // when
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(inquiry));

        // then
        assertThatThrownBy(() -> inquiryService.getInquiryDetailedResponse(inquiryId))
                .isInstanceOf(InquiryException.class)
                .hasMessage("해당 문의글을 열람할 권한이 없습니다.");
    }

    @Test
    @DisplayName("권한이 없는 문의글을 조회하는 경우 예외가 발생한다. - 비밀글의 경우, 로그인한 사용자")
    void getInquiryDetailedResponseWithLoggedInMemberFailureBySecretInquiry() {
        // given
        long inquiryId = 1L;
        long memberId = 1L;
        long anotherMemberId = 2L;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Inquiry inquiry = Inquiry.builder()
                .id(inquiryId)
                .title("title")
                .content("content")
                .existAnswer(false)
                .isSecret(true)
                .member(member)
                .build();

        // when
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(inquiry));

        // then
        assertThatThrownBy(() -> inquiryService.getInquiryDetailedResponse(inquiryId, anotherMemberId))
                .isInstanceOf(InquiryException.class)
                .hasMessage("해당 문의글을 열람할 권한이 없습니다.");
    }

}