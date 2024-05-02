package com.ttarum.member.domain;

import com.ttarum.item.domain.Item;
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
    private Member member;

    @Column(name = "item_id", nullable = false, columnDefinition = "int")
    private Item item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WishlistId entity = (WishlistId) o;
        return Objects.equals(this.item, entity.item) &&
                Objects.equals(this.member, entity.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, member);
    }

}