package com.ttarum.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(name = "AddressUpsertRequest", description = "배송지 추가/수정 요청 DTO")
public class AddressUpsertRequest {
    @Schema(description = "배송지 별칭", example = "집")
    private final String addressAlias;
    @Schema(description = "수령인", example = "홍길동")
    private final String recipient;
    @Schema(description = "배송지 주소", example = "서울시 강남구 역삼동 123-456")
    private final String address;
    @Schema(description = "상세 주소", example = "아파트 123동 456호")
    private final String detailAddress;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private final String phoneNumber;
    @Schema(description = "기본 배송지 여부", example = "true")
    private final boolean isDefault;
}
