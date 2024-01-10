package com.ttarum.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class WishListId implements Serializable {
    private static final long serialVersionUID = -195332860357327664L;
    @Column(name = "user_id", nullable = false, columnDefinition = "int")
    private Long userId;

    @Column(name = "item_id", nullable = false, columnDefinition = "int")
    private Long itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WishListId entity = (WishListId) o;
        return Objects.equals(this.itemId, entity.itemId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, userId);
    }

}