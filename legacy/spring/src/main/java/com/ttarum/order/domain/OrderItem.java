package com.ttarum.order.domain;

import com.ttarum.item.domain.Item;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "order_item")
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, columnDefinition = "int")
    private Order order;

    @MapsId("itemId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, columnDefinition = "int")
    private Item item;

    @Column(name = "amount", nullable = false, columnDefinition = "int")
    private Long amount;

    public OrderItem(Order order, Item item, Long amount) {
        this.id = new OrderItemId(order.getId(), item.getId());
        this.order = order;
        this.item = item;
        this.amount = amount;
    }
}