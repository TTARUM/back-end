package com.ttarum.item.domain;

import com.ttarum.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "item")
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, length = 300)
    private String description;

    @Column(name = "price", nullable = false, columnDefinition = "int")
    private Integer price;

    @Column(name = "item_image_url", nullable = false, length = 200)
    private String itemImageUrl;

    @Column(name = "item_description_image_url", length = 200)
    private String itemDescriptionImageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}