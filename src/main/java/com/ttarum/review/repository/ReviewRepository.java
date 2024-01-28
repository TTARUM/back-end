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
            r.member_id = :memberId AS isOwnReview
            FROM Review r, Member m
            WHERE r.item_id = :itemId AND m.id = r.member_id
                        """, nativeQuery = true)
    List<ReviewResponse> findReviewResponseByItemId(@Param("itemId") Long itemId, Pageable pageable, @Param("memberId") Long memberId);

    @Query("""
            SELECT new com.ttarum.review.dto.response.ReviewResponse(r.id, m.nickname, r.content, r.star, r.createdAt, false)
            FROM Review r
            LEFT JOIN FETCH Member m
            ON m.id = r.member.id
            WHERE r.item.id = :itemId
            """)
    List<ReviewResponse> findReviewResponseByItemId(@Param("itemId") Long itemId, Pageable pageable);

    @Query("""
            SELECT ri
            FROM ReviewImage ri
            WHERE ri.review.id IN (:reviewIds)
            """)
    List<ReviewImage> findReviewImageByReviewId(@Param("reviewIds") List<Long> reviewIds);
}
