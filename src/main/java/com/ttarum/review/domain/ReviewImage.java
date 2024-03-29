package com.ttarum.review.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "review_image")
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int")
    private Long id;

    @Column(name = "file_url", nullable = false, length = 100)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "`order`", nullable = false)
    private Integer order;

    public static ReviewImage of(final String fileUrl, final Review review, final int order) {
        return ReviewImage.builder()
                .fileUrl(fileUrl)
                .review(review)
                .order(order)
                .build();
    }
}