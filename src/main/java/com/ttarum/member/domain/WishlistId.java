package com.ttarum.member.domain;

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
public class WishlistId implements Serializable {
    private static final long serialVersionUID = -195332860357327664L;
    @Column(name = "member_id", nullable = false, columnDefinition = "int")
    private Long memberId;

    @Column(name = "item_id", nullable = false, columnDefinition = "int")
    private Long itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WishlistId entity = (WishlistId) o;
        return Objects.equals(this.itemId, entity.itemId) &&
                Objects.equals(this.memberId, entity.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, memberId);
    }

}