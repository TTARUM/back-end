package com.ttarum.review.repository;

import com.ttarum.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    @Query("SELECT ri.fileUrl FROM ReviewImage ri WHERE ri.review.id = :reviewId")
    List<String> findUrlsByReviewId(@Param("reviewId") long reviewId);
}
