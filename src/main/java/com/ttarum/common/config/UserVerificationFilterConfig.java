package com.ttarum.common.config;

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
    public FilterRegistrationBean<UserVerificationFilter> userVerificationFilterRegistration(final MemberRepository memberRepository) {
        FilterRegistrationBean<UserVerificationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new UserVerificationFilter(memberRepository));
        filterRegistrationBean.setUrlPatterns(List.of(
                "/api/items/list",
                "/api/reviews"
        ));
        return filterRegistrationBean;
    }
}
