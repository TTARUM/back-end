package com.ttarum.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(name = "AddressUpsertRequest", description = "배송지 추가/수정 요청 DTO")
public class AddressUpsertRequest {
    @Schema(description = "배송지 주소", example = "서울시 강남구 역삼동 123-456")
    private final String address;
}
