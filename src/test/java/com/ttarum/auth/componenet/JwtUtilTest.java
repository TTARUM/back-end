package com.ttarum.auth.componenet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateAndExtractToken() {
        JwtUtil jwtUtil = new JwtUtil("secret-key-for-jwt-01010101010101");
        Long memberId = 1234L;

        assertDoesNotThrow(() -> jwtUtil.generateToken(memberId));
        String jws = jwtUtil.generateToken(memberId);

        Long result = Long.parseLong(jwtUtil.extractMemberId(jws));
        assertEquals(memberId, result);
    }
}