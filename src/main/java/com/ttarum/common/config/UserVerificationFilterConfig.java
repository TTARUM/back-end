package com.ttarum.common.config;

import com.ttarum.common.filter.UserVerificationFilter;
import com.ttarum.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class UserVerificationFilterConfig {

    @Bean
    public FilterRegistrationBean<UserVerificationFilter> userVerificationFilterRegistration(final UserRepository userRepository) {
        FilterRegistrationBean<UserVerificationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new UserVerificationFilter(userRepository));
        filterRegistrationBean.setUrlPatterns(List.of(
                "/api/items/list"
        ));
        return filterRegistrationBean;
    }
}
