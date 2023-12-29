package com.ttarum.order.domain;

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
public class OrderItemId implements Serializable {
    private static final long serialVersionUID = 898633847107699104L;
    @Column(name = "order_id", columnDefinition = "int UNSIGNED not null")
    private Long orderId;

    @Column(name = "item_id", columnDefinition = "int UNSIGNED not null")
    private Long itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderItemId entity = (OrderItemId) o;
        return Objects.equals(this.itemId, entity.itemId) &&
                Objects.equals(this.orderId, entity.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, orderId);
    }

}