package com.ttarum.common.filter;

import com.ttarum.auth.componenet.JwtUtil;
import com.ttarum.common.dto.user.LoggedInUser;
import com.ttarum.common.dto.user.UnLoggedInUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.member.domain.Member;
import com.ttarum.member.repository.MemberRepository;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * {@link HttpServletRequest}에 JWT 토큰이 있을 경우 애트리뷰트에 {@link User}를 저장합니다.
 */
@NonNullApi
@Slf4j
@RequiredArgsConstructor
public class UserVerificationFilter extends OncePerRequestFilter {

    private static final String AUTHENTICATION = "authentication";
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        User user = new UnLoggedInUser();

        String jwt = jwtUtil.getJwtFromRequest(request);
        if (jwtUtil.validateToken(jwt)) {
            String memberId = jwtUtil.extractMemberId(jwt);
            Optional<Member> m = memberRepository.findById(Long.parseLong(memberId));
            if (m.isPresent()) {
                user = LoggedInUser.builder()
                        .id(m.get().getId())
                        .build();
            }
        }
        request.setAttribute(AUTHENTICATION, user);
        filterChain.doFilter(request, response);
    }
}
