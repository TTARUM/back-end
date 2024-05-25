package com.ttarum.common.config;

import com.ttarum.auth.componenet.JwtUtil;
import com.ttarum.auth.filter.ExceptionHandlerFilter;
import com.ttarum.auth.filter.JwtAuthenticationFilter;
import com.ttarum.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(request -> {
                            var configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                            configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
                            configuration.setAllowedHeaders(Arrays.asList("*"));
                            configuration.setAllowCredentials(true);
                            return configuration;
                        })
                );

        http
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/api/members/register").permitAll()
                        .requestMatchers("/api/items/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/items/list").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inquiries").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inquiries/list").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/mail/send").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/mail/check").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/mail/send/find-id").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/mail/check/find-id").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/members/mail/find-id").permitAll()
                        .anyRequest().authenticated()
                );

        http
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(userDetailsService, jwtUtil);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
