package com.ttarum.member.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@EqualsAndHashCode
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int")
    private Long id;

    @Setter
    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, columnDefinition = "int")
    private Member member;

    @Column(name = "last_used_at", nullable = false)
    private Instant lastUsedAt;

    public void updateLastUsedAt() {
        this.lastUsedAt = Instant.now();
    }

    @PrePersist
    public void prePersist() {
        this.lastUsedAt = Instant.now();
    }
}