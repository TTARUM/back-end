package com.ttarum.auth.componenet;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
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

    public String extractMemberId(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
