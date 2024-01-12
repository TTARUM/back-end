package com.ttarum.common.filter;

import com.ttarum.common.dto.user.UnLoggedInUser;
import com.ttarum.common.dto.user.User;
import com.ttarum.common.dto.user.LoggedInUser;
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
import java.util.Objects;

@NonNullApi
@Slf4j
@RequiredArgsConstructor
public class UserVerificationFilter extends OncePerRequestFilter {

    private static final String AUTHENTICATION = "authentication";
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        User user;
        if (Objects.isNull(authorization)) {
            log.info("Not Logged in user is coming");
            user = new UnLoggedInUser();
        } else { // FIXME 로그인한 유저인지 확인할 수 있는 로직 필요(토큰 구현 시 작성)
            user = LoggedInUser.builder()
                    .build();
        }
        request.setAttribute(AUTHENTICATION, user);
        filterChain.doFilter(request, response);
    }
}
