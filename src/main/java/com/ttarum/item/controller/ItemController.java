package com.ttarum.item.controller;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.item.dto.response.ItemDetailResponse;
import com.ttarum.item.dto.response.ItemSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

@Tag(name = "item", description = "제품")
public interface ItemController {

    /**
     * 특정 제품의 상세정보 조회 메서드
     *
     * @param itemId 제품의 Id 값
     * @return 제품의 상세정보
     */
    @Operation(summary = "특정 제품의 상세정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 제품")
    })
    @Parameter(name = "id", description = "제품의 아이디", example = "1", required = true)
    @GetMapping
    ResponseEntity<ItemDetailResponse> getDetail(final Long itemId);

    /**
     * 요약된 제품 정보에 대한 검색 메서드
     *
     * @param query 검색어
     * @param user  로그인한 사용자 여부를 확인하기 위한 객체
     * @param page  페이지 넘버
     * @param size  아이템 사이즈
     * @return 검색된 제품 목록
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Operation(summary = "이름으로 제품 검색")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "성공")
    @Parameters(value = {
            @Parameter(name = "query", description = "검색어"),
            @Parameter(name = "user", hidden = true),
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "1"),
            @Parameter(name = "size", description = "한 페이지당 제품 수 (기본 값 9개)", example = "9")
    })
    @GetMapping
    ResponseEntity<ItemSummaryResponse> getSummary(@RequestParam(required = false) final String query,
                                                         @VerificationUser final User user,
                                                         final Optional<Integer> page,
                                                         final Optional<Integer> size);
}
