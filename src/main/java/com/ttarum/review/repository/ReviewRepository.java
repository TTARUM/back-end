package com.ttarum.review.repository;

import com.ttarum.review.domain.Review;
import com.ttarum.review.domain.ReviewImage;
import com.ttarum.review.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = """
            SELECT r.id AS id,
            m.nickname AS nickname,
            r.content AS content,
            star AS rating,
            r.created_at AS createdAt,
            FROM Review r, Member m
            WHERE r.item_id = :itemId AND m.id = r.member_id
                        """, nativeQuery = true)
    List<ReviewResponse> findReviewResponseByItemId(@Param("itemId") Long itemId, Pageable pageable);

    @Query("""
            SELECT ri
            FROM ReviewImage ri
            WHERE ri.review.id IN (:reviewIds)
            """)
    List<ReviewImage> findReviewImageByReviewId(@Param("reviewIds") List<Long> reviewIds);
}
