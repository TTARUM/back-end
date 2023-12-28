package com.ttarum.order.domain;

import com.ttarum.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "comment", nullable = false, length = 100)
    private String comment;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "recipient", nullable = false, length = 20)
    private String recipient;

    @Column(name = "price", columnDefinition = "int UNSIGNED not null")
    private Long price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}