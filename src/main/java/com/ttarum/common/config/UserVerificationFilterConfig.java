package com.ttarum.common.config;

import com.ttarum.auth.componenet.JwtUtil;
import com.ttarum.common.filter.UserVerificationFilter;
import com.ttarum.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class UserVerificationFilterConfig {

    @Bean
    public FilterRegistrationBean<UserVerificationFilter> userVerificationFilterRegistration(final MemberRepository memberRepository, final JwtUtil jwtUtil) {
        FilterRegistrationBean<UserVerificationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new UserVerificationFilter(memberRepository, jwtUtil));
        filterRegistrationBean.setUrlPatterns(List.of(
                "/api/items/list",
                "/api/inquiries/list",
                "/api/inquiries",
                "/api/items/similar-price"
        ));
        return filterRegistrationBean;
    }
}
