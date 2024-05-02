package com.ttarum.review.repository;

import com.ttarum.review.domain.Review;
import com.ttarum.review.domain.ReviewImage;
import com.ttarum.review.dto.response.ReviewResponse;
import com.ttarum.review.dto.response.ReviewUpdateResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * {@link Long itemId}가 Id 값인 제품의 모든 {@link Review}를 {@link ReviewResponse} 리스트로 반환합니다.
     *
     * @param itemId   제품의 Id 값
     * @param pageable pageable
     * @return {@link ReviewResponse} 리스트
     */
    @Query(value = """
            SELECT r.id AS id,
            m.nickname AS nickname,
            r.content AS content,
            star AS rating,
            r.created_at AS createdAt,
            FROM Review r, Member m
            WHERE r.item_id = :itemId AND m.id = r.member_id AND r.is_deleted = false
                        """, nativeQuery = true)
    List<ReviewResponse> findReviewResponseByItemId(@Param("itemId") Long itemId, Pageable pageable);

    /**
     * {@link Review}의 Id 값 리스트를 받아 해당 {@link Review}들의 {@link ReviewImage}들을 리스트로 반환합니다.
     *
     * @param reviewIds {@link Review}의 {@link List Id 값 리스트}
     * @return {@link ReviewImage} 리스트
     */
    @Query("""
            SELECT ri
            FROM ReviewImage ri
            WHERE ri.review.id IN (:reviewIds)
            """)
    List<ReviewImage> findReviewImageByReviewId(@Param("reviewIds") List<Long> reviewIds);

    @Query("SELECT r FROM Review r WHERE r.order.id = :orderId AND r.item.id = :itemId")
    Optional<Review> findReviewByOrderIdAndItemId(@Param("orderId") long orderId, @Param("itemId")  long itemId);

    @Query("""
            SELECT new com.ttarum.review.dto.response.ReviewUpdateResponse(r.item.name, r.content, r.createdAt)
            FROM Review r
            WHERE r.id = :reviewId
            """)
    ReviewUpdateResponse findReviewUpdateResponseById(@Param("reviewId") long reviewId);

    @Query(value = """
            SELECT r.id AS id,
            m.nickname AS nickname,
            r.content AS content,
            star AS rating,
            r.created_at AS createdAt,
            FROM Review r, Member m
            WHERE r.member_id = :memberId AND r.is_deleted = false
                        """, nativeQuery = true)
    List<ReviewResponse> findReviewResponseByMemberId(@Param("memberId") long memberId, Pageable pageable);
}
