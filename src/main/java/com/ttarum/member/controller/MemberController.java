package com.ttarum.member.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.member.dto.request.NormalMemberRegister;
import com.ttarum.member.dto.response.WishListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Tag(name = "Member", description = "회원")
public interface MemberController {
    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패")
    })
    @PostMapping(consumes = "application/json")
    void registerNormalMember(@RequestBody NormalMemberRegister dto);

    @Operation(summary = "찜 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @Parameters(value = {
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "1"),
            @Parameter(name = "size", description = "페이지 당 컨텐츠 갯수 (기본 값 8)", example = "8")
    })
    @GetMapping
    ResponseEntity<WishListResponse> getWishList(@AuthenticationPrincipal CustomUserDetails user,
                                                 Optional<Integer> page,
                                                 Optional<Integer> size);
}
