package com.ttarum.review.domain;

import com.ttarum.common.domain.UpdatableEntity;
import com.ttarum.item.domain.Item;
import com.ttarum.member.domain.Member;
import com.ttarum.order.domain.Order;
import com.ttarum.review.dto.request.ReviewUpdateRequest;
import com.ttarum.review.validator.ReviewValidator;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, columnDefinition = "int")
    private Item item;

    @Column(name = "title", nullable = false)
    private String title;

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

    public void setInitialForeignEntity(final Member member, final Order order, final Item item) {
        this.member = member;
        this.order = order;
        this.item = item;
    }
}