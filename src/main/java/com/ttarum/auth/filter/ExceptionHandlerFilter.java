package com.ttarum.auth.filter;

import com.ttarum.common.exception.TtarumException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            doFilter(request, response, filterChain);
        } catch (TtarumException e) {
            response.setStatus(e.getStatus().value());
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(e.getMessage());
        }
    }
}
