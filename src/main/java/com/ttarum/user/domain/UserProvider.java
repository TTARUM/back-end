package com.ttarum.user.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "user_provider")
public class UserProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

}