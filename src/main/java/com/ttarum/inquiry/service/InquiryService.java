package com.ttarum.inquiry.service;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.dto.request.InquiryCreationRequest;
import com.ttarum.inquiry.exception.InquiryException;
import com.ttarum.inquiry.repository.InquiryRepository;
import com.ttarum.item.domain.Item;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Member;
import com.ttarum.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public long postInquiry(final InquiryCreationRequest request, final long memberId) {
        Inquiry inquiry = request.transferToInquiry();
        Item item = getItemById(request);
        Member member = getMemberById(memberId);
        inquiry.setMember(member);
        inquiry.setItem(item);

        return inquiryRepository.save(inquiry).getId();
    }

    private Member getMemberById(final long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new InquiryException("해당 회원이 존재하지 않습니다."));
    }

    private Item getItemById(final InquiryCreationRequest request) {
        return itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new InquiryException("해당 제품이 존재하지 않습니다."));
    }
}
