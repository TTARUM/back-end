package com.ttarum.member.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "address_alias", nullable = false, length = 45)
    private String addressAlias;

    @Column(name = "recipient", nullable = false, length = 20)
    private String recipient;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "detail_address", nullable = false, length = 100)
    private String detailAddress;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, columnDefinition = "int")
    private Member member;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    public void update(final Address newAddress) {
        this.addressAlias = newAddress.getAddressAlias();
        this.recipient = newAddress.getRecipient();
        this.address = newAddress.getAddress();
        this.detailAddress = newAddress.getDetailAddress();
        this.phoneNumber = newAddress.getPhoneNumber();
        this.isDefault = newAddress.isDefault();
    }

    public void nonDefault() {
        this.isDefault = false;
    }
}