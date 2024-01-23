package com.ttarum.member.controller;

import com.ttarum.member.dto.request.NormalMemberRegister;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member", description = "회원")
public interface MemberController {
    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패")
    })
    @PostMapping(consumes = "application/json")
    void registerNormalMember(@RequestBody NormalMemberRegister dto);
}
