package com.ttarum.member.domain;

import com.ttarum.common.domain.UpdatableEntity;
import com.ttarum.order.domain.Order;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "member")
public class Member extends UpdatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int")
    private Long id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Setter
    @Column(name = "image_url", length = 100)
    private String imageUrl;

    @Setter
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        this.isDeleted = false;
    }

    public boolean isMyOrder(final Order order) {
        return equals(order.getMember());
    }
}