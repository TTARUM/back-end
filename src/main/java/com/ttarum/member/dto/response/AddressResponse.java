package com.ttarum.member.dto.response;

import com.ttarum.member.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "주소 조회 DTO")
public class AddressResponse {
    private final long addressId;
    private final String address;

    public static AddressResponse fromAddress(Address address) {
        return new AddressResponse(address.getId(), address.getAddress());
    }
}
