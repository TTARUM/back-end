package com.ttarum.member.domain;

import com.ttarum.item.domain.Item;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "cart")
public class Cart {
    @EmbeddedId
    private CartId id;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, columnDefinition = "int")
    private Member member;

    @MapsId("itemId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, columnDefinition = "int")
    private Item item;

    @Column(name = "amount", nullable = false, columnDefinition = "int")
    private int amount;

    public void addAmount(final int amount) {
        this.amount += amount;
    }

    public void updateAmount(final int amount) {
        this.amount = amount;
    }
}