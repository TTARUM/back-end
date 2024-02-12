package com.ttarum.auth.componenet;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret-key}") String key) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }

    public String generateToken(Long memberId) {
        return Jwts.builder()
                .subject(memberId.toString())
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String jws) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Long.parseLong(Jwts.parser().verifyWith(key).build().parseSignedClaims(jws).getPayload().getSubject());
            return true;
        } catch (JwtException e) {
            log.error("JWT Error: ", e);
            return false;
        }
    }

    public Long extractMemberId(String token) {
            return Long.parseLong(
                Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject()
            );
    }
}
