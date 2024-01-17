package com.ttarum.review.repository;

import com.ttarum.review.domain.Review;
import com.ttarum.review.dto.response.ReviewResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
            SELECT new com.ttarum.review.dto.response.ReviewResponse(m.nickname, r.content, r.star, ri.fileUrl, r.createdAt)
            FROM Review r
            LEFT JOIN FETCH ReviewImage ri
            ON ri.review.id = r.id
            LEFT JOIN FETCH Member m
            ON m.id = r.member.id
            WHERE r.item.id = :itemId
            """)
    List<ReviewResponse> findReviewResponseByItemId(@Param("itemId") Long itemId);
}
