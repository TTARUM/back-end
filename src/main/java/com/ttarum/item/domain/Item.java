package com.ttarum.item.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, length = 300)
    private String description;

    @Column(name = "price", columnDefinition = "int UNSIGNED not null")
    private Long price;

    @Column(name = "volume", columnDefinition = "int UNSIGNED not null")
    private Long volume;

    @Column(name = "item_image_url", nullable = false, length = 100)
    private String itemImageUrl;

    @Column(name = "item_description_image_url", nullable = false, length = 100)
    private String itemDescriptionImageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

}