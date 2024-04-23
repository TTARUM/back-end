package com.ttarum.member.dto.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.ttarum.member.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "주소 조회 DTO")
public class AddressResponse {
    private final long addressId;
    private final String addressAlias;
    private final String recipient;
    private final String address;
    private final String detailAddress;
    private final String phoneNumber;
    private final boolean isDefault;

    @JsonGetter("isDefault")
    public boolean isDefault() {
        return isDefault;
    }

    public static AddressResponse fromAddress(Address address) {
        return AddressResponse.builder()
                .addressId(address.getId())
                .addressAlias(address.getAddressAlias())
                .recipient(address.getRecipient())
                .address(address.getAddress())
                .detailAddress(address.getDetailAddress())
                .phoneNumber(address.getPhoneNumber())
                .isDefault(address.isDefault())
                .build();
    }
}
