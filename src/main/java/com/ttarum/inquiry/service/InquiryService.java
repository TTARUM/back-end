package com.ttarum.inquiry.service;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.domain.InquiryAnswer;
import com.ttarum.inquiry.dto.response.InquiryDetailedResponse;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import com.ttarum.inquiry.dto.response.answer.InquiryAnswerNotExistResponse;
import com.ttarum.inquiry.dto.response.answer.InquiryAnswerResponse;
import com.ttarum.inquiry.exception.InquiryAnswerNotFoundException;
import com.ttarum.inquiry.exception.InquiryException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    /**
     * 특정 제품의 요약된 문의글 조회
     * 로그인한 경우 해당 메서드를 호출합니다.
     * 로그인한 회원의 ID를 이용해 자신의 문의글인지 여부를 확인할 수 있습니다.
     *
     * @param itemId   특정 제품의 ID
     * @param memberId 로그인한 회원의 ID
     * @param pageable 페이지
     * @return 요약된 문의글 리스트
     * @throws InquiryException 제품이 존재하지 않을 경우 발생하는 예외
     */
    public List<InquirySummaryResponse> getInquirySummaryResponseList(final long itemId, final long memberId, final Pageable pageable) {
        checkItemExistence(itemId);
        return inquiryRepository.findInquirySummaryByItemIdAndMemberId(itemId, memberId, pageable);
    }

    private void checkItemExistence(final long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new ItemNotFoundException();
        }
    }

    /**
     * 특정 제품의 요약된 문의글 조회
     * 로그인하지 않은 경우 해당 메서드를 호출합니다.
     * 로그인하지 않았으므로 자신의 문의글인지 확인할 수 없습니다.
     *
     * @param itemId   특정 제품의 ID
     * @param pageable 페이지
     * @return 요약된 문의글 리스트
     * @throws InquiryException 제품이 존재하지 않을 경우 발생하는 예외
     */
    public List<InquirySummaryResponse> getInquirySummaryResponseList(final long itemId, final Pageable pageable) {
        checkItemExistence(itemId);
        return inquiryRepository.findInquirySummaryByItemId(itemId, pageable);
    }

    /**
     * 문의글의 ID값을 이용해 문의글을 조회합니다.
     * 로그인한 경우 해당 메서드를 호출합니다.
     * 로그인한 회원의 ID 값을 이용해 조회할 수 있는지 확인합니다.
     * 비밀글의 경우 로그인한 회원의 문의글이 아닐 경우 예외가 발생합니다.
     *
     * @param inquiryId 조회할 문의글의 ID
     * @param memberId  로그인한 회원의 ID
     * @return 문의글의 정보가 담긴 객체
     * @throws InquiryException 문의글이 존재하지 않을 경우, 접근할 수 없는 문의글의 경우 발생하는 예외
     */
    public InquiryDetailedResponse getInquiryDetailedResponse(final long inquiryId, final long memberId) {
        Inquiry inquiry = getInquiryById(inquiryId);
        validateAccessToInquiry(memberId, inquiry);

        InquiryAnswerResponse inquiryAnswerResponse;
        if (Boolean.TRUE.equals(inquiry.getExistAnswer())) {
            InquiryAnswer inquiryAnswer = getInquiryAnswerByInquiryId(inquiryId);
            inquiryAnswerResponse = InquiryAnswerResponse.of(inquiryAnswer);
        } else {
            inquiryAnswerResponse = new InquiryAnswerNotExistResponse();
        }
        InquiryDetailedResponse response = InquiryDetailedResponse.of(inquiry, inquiryAnswerResponse);

        List<String> imageUrls = inquiryRepository.findInquiryImageByInquiryId(inquiryId);
        for (String imageUrl : imageUrls) {
            response.addImageUrl(imageUrl);
        }
        return response;
    }

    private Inquiry getInquiryById(final long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(InquiryNotFoundException::new);
    }

    private void validateAccessToInquiry(final long memberId, final Inquiry inquiry) {
        if (Boolean.TRUE.equals(inquiry.getIsSecret()) && !inquiry.getMember().getId().equals(memberId)) {
            throw new InquiryForbiddenException();
        }
    }

    private InquiryAnswer getInquiryAnswerByInquiryId(final long inquiryId) {
        return inquiryRepository.findAnswerByInquiryId(inquiryId)
                .orElseThrow(InquiryAnswerNotFoundException::new);
    }

    /**
     * 문의글의 ID값을 이용해 문의글을 조회합니다.
     * 로그인하지 않은 경우 해당 메서드를 호출합니다.
     * 비밀글의 경우 예외가 발생합니다.
     *
     * @param inquiryId 조회할 문의글의 ID
     * @return 문의글의 정보가 담긴 객체
     * @throws InquiryException 문의글이 존재하지 않을 경우, 접근할 수 없는 문의글의 경우 발생하는 예외
     */
    public InquiryDetailedResponse getInquiryDetailedResponse(final long inquiryId) {
        Inquiry inquiry = getInquiryById(inquiryId);
        validateAccessToInquiry(inquiry);

        InquiryAnswerResponse inquiryAnswerResponse;
        if (Boolean.TRUE.equals(inquiry.getExistAnswer())) {
            InquiryAnswer inquiryAnswer = getInquiryAnswerByInquiryId(inquiryId);
            inquiryAnswerResponse = InquiryAnswerResponse.of(inquiryAnswer);
        } else {
            inquiryAnswerResponse = new InquiryAnswerNotExistResponse();
        }
        InquiryDetailedResponse response = InquiryDetailedResponse.of(inquiry, inquiryAnswerResponse);

        List<String> imageUrls = inquiryRepository.findInquiryImageByInquiryId(inquiryId);
        for (String imageUrl : imageUrls) {
            response.addImageUrl(imageUrl);
        }
        return response;
    }

    private void validateAccessToInquiry(final Inquiry inquiry) {
        if (Boolean.TRUE.equals(inquiry.getIsSecret())) {
            throw new InquiryForbiddenException();
        }
    }

    /**
     * 문의글 등록 메서드
     *
     * @param request  등록할 문의글의 데이터가 담긴 객체
     * @param memberId 등록할 회원의 ID
     * @return 등록된 문의글의 ID
     * @throws InquiryException 유효하지 않은 회원의 ID, 존재하지 않는 제품에 등록하려는 경우, 제목이나 내용이 비어있을 경우 발생하는 예외
     */
    @Transactional
    public long postInquiryArticle(final InquiryCreationRequest request, final long memberId) {
        Inquiry inquiry = request.transferToInquiry();
        Item item = getItemById(request);
        Member member = getMemberById(memberId);
        inquiry.setMember(member);
        inquiry.setItem(item);

        return inquiryRepository.save(inquiry).getId();
    }

    private Member getMemberById(final long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Item getItemById(final InquiryCreationRequest request) {
        return itemRepository.findById(request.getItemId())
                .orElseThrow(ItemNotFoundException::new);
    }
}
