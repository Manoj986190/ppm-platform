package com.ppm.backend.config;

import com.ppm.backend.security.JwtAuthFilter;
import com.ppm.backend.security.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityFilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthFilter(jwtUtil));

        // 🔴 THIS MUST MATCH EXACTLY
        registration.addUrlPatterns("/api/*");

        return registration;
    }
}
