package com.ttarum.auth.controller;

import com.ttarum.auth.dto.request.NormalLoginRequest;
import com.ttarum.auth.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "auth", description = "로그인")
public interface AuthController {

    @Operation(summary = "일반 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "회원 검증 실패")
    })
    @Parameter(name = "dto", description = "일반 로그인 요청", required = true)
    @PostMapping
    ResponseEntity<LoginResponse> login(final NormalLoginRequest dto);
}
