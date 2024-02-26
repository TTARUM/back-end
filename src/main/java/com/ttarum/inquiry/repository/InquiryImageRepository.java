package com.ttarum.inquiry.repository;

import com.ttarum.inquiry.domain.InquiryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InquiryImageRepository extends JpaRepository<InquiryImage, Long> {

    /**
     * {@link Long inquiryId}가 Id 값인 문의글의 {@link InquiryImage}들의 {@link String fileUrl} 리스트를 반환합니다.
     *
     * @param inquiryId 문의글의 Id 값
     * @return {@link String fileUrl} 리스트
     */
    @Query("SELECT ii.fileUrl FROM InquiryImage ii WHERE ii.inquiry.id = :inquiryId")
    List<String> findInquiryImageByInquiryId(@Param("inquiryId") long inquiryId);
}
