package com.ttarum.auth.controller;

import com.ttarum.auth.dto.request.NormalLoginRequest;
import com.ttarum.auth.dto.response.LoginResponse;
import com.ttarum.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final NormalLoginRequest dto) {
        LoginResponse loginResponse = authService.normalLogin(dto);
        return ResponseEntity.ok(loginResponse);
    }
}
