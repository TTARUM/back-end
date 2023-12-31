package com.ttarum.order.domain;

import com.ttarum.common.domain.BaseEntity;
import com.ttarum.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "`order`")
public class Order extends BaseEntity {
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

}