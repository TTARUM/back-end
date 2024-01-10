package com.ttarum.review.domain;

import com.ttarum.common.domain.UpdatableEntity;
import com.ttarum.item.domain.Item;
import com.ttarum.user.domain.User;
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
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "int")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, columnDefinition = "int")
    private Item item;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "star", nullable = false, columnDefinition = "tinyint")
    private Short star;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Override
    public void prePersist() {
        super.prePersist();
        this.isDeleted = false;
    }
}