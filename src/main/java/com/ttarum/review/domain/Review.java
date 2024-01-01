package com.ttarum.review.domain;

import com.ttarum.common.domain.BaseEntity;
import com.ttarum.common.domain.UpdatableEntity;
import com.ttarum.item.domain.Item;
import com.ttarum.user.domain.User;
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
    @Column(name = "id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "star", columnDefinition = "tinyint UNSIGNED not null")
    private Short star;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Override
    public void prePersist() {
        super.prePersist();
        this.isDeleted = false;
    }
}