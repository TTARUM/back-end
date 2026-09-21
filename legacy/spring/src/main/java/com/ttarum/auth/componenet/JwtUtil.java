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

    /**
     * 회원의 Id 값으로 JWT를 생성합니다.
     *
     * @param memberId 회원의 Id 값
     * @return JWT
     */
    public String generateToken(Long memberId) {
        return Jwts.builder()
                .subject(memberId.toString())
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    /**
     * {@link HttpServletRequest}에서 {@code Authorization} 헤더에 있는 토큰을 반환합니다.
     *
     * @param request {@link HttpServletRequest}
     * @return JWT, 헤더가 존재하지 않거나 토큰이 없을 경우 {@code null}을 반환합니다.
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 토큰의 유효성 검사를 합니다.
     *
     * @param jws JWT
     * @return 유효성 검사에 실패하면 {@code false}, 아니면 {@code true}를 반환합니다.
     */
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

    /**
     * 토큰에서 회원의 Id 값을 추출한 후 반환합니다.
     *
     * @param token JWT
     * @return 회원의 Id 값
     */
    public String extractMemberId(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
