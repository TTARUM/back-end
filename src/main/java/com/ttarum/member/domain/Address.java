package com.ttarum.member.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
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

    @Column(name = "is_recent", nullable = false, columnDefinition = "boolean default false")
    private boolean isRecent;
}