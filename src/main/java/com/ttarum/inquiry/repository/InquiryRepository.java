package com.ttarum.inquiry.repository;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.domain.InquiryAnswer;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import com.ttarum.inquiry.domain.InquiryImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    /**
     * {@link Long itemId}가 Id 값인 제품의 문의글을 {@link InquirySummaryResponse} 리스트로 반환합니다.
     * {@link Long memberId}가 Id 값인 회원의 리뷰 여부를 포함합니다.
     *
     * @param itemId   제품의 Id 값
     * @param memberId 회원의 Id 값
     * @param pageable pageable
     * @return 조회된 {@link InquirySummaryResponse} 리스트
     */
    @Query(value = """
            SELECT
            i.id AS id,
            i.title AS title,
            i.is_secret AS isSecretInquiry,
            i.member_id = :memberId,
            i.exist_answer AS hasAnswer,
            m.name AS memberName,
            i.created_at AS createdAt
            FROM Inquiry i
            LEFT OUTER JOIN Member m
            ON i.member_id = m.id
            WHERE i.item_id = :itemId
            """, nativeQuery = true)
    List<InquirySummaryResponse> findInquirySummaryByItemIdAndMemberId(@Param("itemId") long itemId, @Param("memberId") long memberId, Pageable pageable);

    /**
     * {@link Long itemId}가 Id 값인 제품의 문의글을 {@link InquirySummaryResponse} 리스트로 반환합니다.
     *
     * @param itemId   제품의 Id 값
     * @param pageable pageable
     * @return 조회된 {@link InquirySummaryResponse} 리스트
     */
    @Query(value = """
            SELECT
            i.id AS id,
            i.title AS title,
            i.is_secret AS isSecretInquiry,
            false,
            i.exist_answer AS hasAnswer,
            m.name AS memberName,
            i.created_at AS createdAt
            FROM inquiry i
            LEFT OUTER JOIN Member m
            ON i.member_id = m.id
            WHERE i.item_id = :itemId
            """, nativeQuery = true)
    List<InquirySummaryResponse> findInquirySummaryByItemId(@Param("itemId") long itemId, Pageable pageable);

    /**
     * {@link Long inquiryId}가 Id 값인 문의글의 {@link InquiryImage}들의 {@link String fileUrl} 리스트를 반환합니다.
     *
     * @param inquiryId 문의글의 Id 값
     * @return {@link String fileUrl} 리스트
     */
    @Query("SELECT ii.fileUrl FROM InquiryImage ii WHERE ii.inquiry.id = :inquiryId")
    List<String> findInquiryImageByInquiryId(@Param("inquiryId") long inquiryId);

    /**
     * {@link Long inquiryId}가 Id 값인 문의글의 답변을 반환합니다.
     *
     * @param inquiryId 문의글의 Id 값
     * @return {@link InquiryAnswer}
     */
    @Query("SELECT ia FROM InquiryAnswer ia WHERE ia.inquiry.id = :inquiryId")
    Optional<InquiryAnswer> findAnswerByInquiryId(@Param("inquiryId") long inquiryId);

}
