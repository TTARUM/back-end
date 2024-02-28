package com.ttarum.review.domain;

import com.ttarum.common.domain.UpdatableEntity;
import com.ttarum.member.domain.Member;
import com.ttarum.order.domain.Order;
import com.ttarum.review.dto.request.ReviewUpdateRequest;
import com.ttarum.review.validator.ReviewValidator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "review")
public class Review extends UpdatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, columnDefinition = "int")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, columnDefinition = "int")
    private Order order;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "star", nullable = false, columnDefinition = "tinyint")
    private Short star;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        this.isDeleted = false;
    }

    public void delete() {
        isDeleted = true;
    }

    public void update(final ReviewUpdateRequest request, final ReviewValidator validator) {
        this.content = request.getContent();
        this.star = request.getRating();
        validate(validator);
    }

    public void validate(final ReviewValidator validator) {
        validator.validateRating(this.star);
    }
}