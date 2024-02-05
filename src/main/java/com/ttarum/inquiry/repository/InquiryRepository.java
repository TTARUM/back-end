package com.ttarum.inquiry.repository;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.domain.InquiryAnswer;
import com.ttarum.inquiry.dto.response.InquirySummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

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

    @Query("SELECT ii.fileUrl FROM InquiryImage ii WHERE ii.inquiry.id = :inquiryId")
    List<String> findInquiryImageByInquiryId(@Param("inquiryId") long inquiryId);

    @Query("SELECT ia FROM InquiryAnswer ia WHERE ia.inquiry.id = :inquiryId")
    Optional<InquiryAnswer> findAnswerByInquiryId(@Param("inquiryId") long inquiryId);

}
