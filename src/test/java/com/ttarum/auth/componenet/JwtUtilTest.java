package com.ttarum.auth.componenet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateAndExtractToken() {
        JwtUtil jwtUtil = new JwtUtil("secret-key-for-test-010101010101010101010101010");
        Long memberId = 1234L;

        assertDoesNotThrow(() -> jwtUtil.generateToken(memberId));
        String jws = jwtUtil.generateToken(memberId);

        Long result = jwtUtil.extractMemberId(jws);
        assertEquals(memberId, result);
    }
}