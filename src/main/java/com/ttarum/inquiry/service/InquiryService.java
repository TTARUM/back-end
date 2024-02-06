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

    /**
     * 문의글 등록 메서드
     *
     * @param request  등록할 문의글의 데이터가 담긴 객체
     * @param memberId 등록할 회원의 ID
     * @return 등록된 문의글의 ID
     * @throws InquiryException 유효하지 않은 회원의 ID, 존재하지 않는 제품에 등록하려는 경우, 제목이나 내용이 비어있을 경우 발생하는 예외
     */
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
