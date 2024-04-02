package com.ttarum.member.domain;

import com.ttarum.common.domain.BaseEntity;
import com.ttarum.item.domain.Item;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "wishlist")
public class Wishlist extends BaseEntity {
    @EmbeddedId
    private WishlistId id;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, columnDefinition = "int")
    private Member member;

    @MapsId("itemId")
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false, columnDefinition = "int")
    private Item item;
}