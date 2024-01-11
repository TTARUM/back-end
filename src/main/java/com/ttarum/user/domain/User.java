package com.ttarum.user.domain;

import com.ttarum.common.domain.UpdatableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "user")
public class User extends UpdatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int not null")
    private Long id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "image_url", length = 100)
    private String imageUrl;

    @OneToMany(mappedBy = "user")
    private List<Address> addressList = new ArrayList<>();

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Override
    public void prePersist() {
        super.prePersist();
        this.isDeleted = false;
    }
}