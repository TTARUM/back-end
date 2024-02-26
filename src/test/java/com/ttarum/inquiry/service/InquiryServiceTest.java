package com.ttarum.inquiry.service;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.domain.InquiryAnswer;
import com.ttarum.inquiry.dto.response.InquiryDetailedResponse;
import com.ttarum.inquiry.dto.response.InquiryImageResponse;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import com.ttarum.inquiry.dto.response.answer.InquiryAnswerExistResponse;
import com.ttarum.inquiry.dto.response.answer.InquiryAnswerNotExistResponse;
import com.ttarum.inquiry.dto.request.InquiryCreationRequest;
import com.ttarum.inquiry.exception.InquiryForbiddenException;
import com.ttarum.inquiry.exception.InquiryNotFoundException;
import com.ttarum.inquiry.repository.InquiryRepository;
import com.ttarum.item.domain.Item;
import com.ttarum.item.exception.ItemNotFoundException;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Member;
import com.ttarum.member.exception.MemberNotFoundException;
import com.ttarum.member.repository.MemberRepository;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquiryServiceTest {

    @Mock
    InquiryRepository inquiryRepository;
    @Mock
    MemberRepository memberRepository;
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
        assertThat(response.getMemberName()).isEqualTo("m********e");
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
                .isInstanceOf(ItemNotFoundException.class);
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
        assertThat(response.getMemberName()).isEqualTo("m********e");
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
                .isInstanceOf(ItemNotFoundException.class);
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
        when(inquiryRepository.findById(inquiryId)).thenThrow(new InquiryNotFoundException());

        // then
        assertThatThrownBy(() -> inquiryService.getInquiryDetailedResponse(inquiryId, memberId))
                .isInstanceOf(InquiryNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 문의글을 조회하는 경우 예외가 발생한다. - 로그인하지 않은 경우")
    void getInquiryDetailedResponseFailureByInvalidInquiryId() {
        // given
        long inquiryId = 1L;

        // when
        when(inquiryRepository.findById(inquiryId)).thenThrow(new InquiryNotFoundException());

        // then
        assertThatThrownBy(() -> inquiryService.getInquiryDetailedResponse(inquiryId))
                .isInstanceOf(InquiryNotFoundException.class);
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
                .isInstanceOf(InquiryForbiddenException.class);
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
                .isInstanceOf(InquiryForbiddenException.class);
    }

    @Test
    @DisplayName("문의글을 등록할 수 있다.")
    void postInquiryArticle() {
        // given
        long itemId = 1L;
        long memberId = 1L;
        Item item = Item.builder()
                .id(1L)
                .build();
        Member member = Member.builder()
                .id(1L)
                .build();
        InquiryCreationRequest request = InquiryCreationRequest.builder()
                .title("title")
                .content("content")
                .isSecret(false)
                .itemId(itemId)
                .build();

        // when
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(inquiryRepository.save(any())).thenReturn(Inquiry.builder().id(1L).build());
        inquiryService.postInquiryArticle(request, memberId);

        // then
        verify(itemRepository, times(1)).findById(itemId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(inquiryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 제품에 대한 문의글을 등록할 경우 예외가 발생한다.")
    void postInquiryArticleFailureByInvalidItemId() {
        // given
        long itemId = 1L;
        long memberId = 1L;
        InquiryCreationRequest request = InquiryCreationRequest.builder()
                .title("title")
                .content("content")
                .isSecret(false)
                .itemId(itemId)
                .build();

        // when
        when(itemRepository.findById(itemId)).thenThrow(new ItemNotFoundException());

        // then
        assertThatThrownBy(() -> inquiryService.postInquiryArticle(request, memberId))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 회원이 문의글을 작성하는 경우 예외가 발생한다.")
    void postInquiryArticleFailureByInvalidMemberId() {
        // given
        long itemId = 1L;
        long memberId = 1L;
        Item item = Item.builder()
                .id(1L)
                .build();
        InquiryCreationRequest request = InquiryCreationRequest.builder()
                .title("title")
                .content("content")
                .isSecret(false)
                .itemId(itemId)
                .build();

        // when
        when(memberRepository.findById(memberId)).thenThrow(new MemberNotFoundException());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));


        // then
        assertThatThrownBy(() -> inquiryService.postInquiryArticle(request, memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }
}