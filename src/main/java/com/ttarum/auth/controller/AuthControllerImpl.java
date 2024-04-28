package com.ttarum.auth.controller;

import com.ttarum.auth.dto.request.NormalLoginRequest;
import com.ttarum.auth.dto.response.LoginResponse;
import com.ttarum.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "auth", description = "로그인")
@RequestMapping("/api/auth")
public class AuthControllerImpl {
    private final AuthService authService;
    /**
     * 로그인 메서드
     * 회원의 로그인 데이터를 받아 로그인에 성공하면 토큰과 함께 회원의 정보를 담은 데이터를 반환합니다.
     *
     * @param dto 회원의 로그인 데이터
     * @return 회원의 정보와 토큰이 담긴 객체
     */
    @Operation(summary = "일반 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "회원 검증 실패")
    })
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final NormalLoginRequest dto) {
        LoginResponse loginResponse = authService.normalLogin(dto);
        return ResponseEntity.ok(loginResponse);
    }
}
