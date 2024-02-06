package com.ttarum.inquiry.service;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.dto.request.InquiryCreationRequest;
import com.ttarum.inquiry.exception.InquiryException;
import com.ttarum.inquiry.repository.InquiryRepository;
import com.ttarum.item.domain.Item;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Member;
import com.ttarum.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    @DisplayName("문의글을 등록할 수 있다.")
    void postInquiry() {
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
        inquiryService.postInquiry(request, memberId);

        // then
        verify(itemRepository, times(1)).findById(itemId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(inquiryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 제품에 대한 문의글을 등록할 경우 예외가 발생한다.")
    void postInquiryFailureByInvalidItemId() {
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
        when(itemRepository.findById(itemId)).thenThrow(new InquiryException("해당 제품이 존재하지 않습니다."));

        // then
        assertThatThrownBy(() -> inquiryService.postInquiry(request, memberId))
                .isInstanceOf(InquiryException.class)
                .hasMessage("해당 제품이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 회원이 문의글을 작성하는 경우 예외가 발생한다.")
    void postInquiryFailureByInvalidMemberId() {
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
        when(memberRepository.findById(memberId)).thenThrow(new InquiryException("해당 회원이 존재하지 않습니다."));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));


        // then
        assertThatThrownBy(() -> inquiryService.postInquiry(request, memberId))
                .isInstanceOf(InquiryException.class)
                .hasMessage("해당 회원이 존재하지 않습니다.");
    }
}