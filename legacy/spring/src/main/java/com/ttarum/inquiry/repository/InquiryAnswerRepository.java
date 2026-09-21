package com.ttarum.inquiry.repository;

import com.ttarum.inquiry.domain.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {

    /**
     * {@link Long inquiryId}가 Id 값인 문의글의 답변을 반환합니다.
     *
     * @param inquiryId 문의글의 Id 값
     * @return {@link InquiryAnswer}
     */
    @Query("SELECT ia FROM InquiryAnswer ia WHERE ia.inquiry.id = :inquiryId")
    Optional<InquiryAnswer> findAnswerByInquiryId(@Param("inquiryId") long inquiryId);
}
