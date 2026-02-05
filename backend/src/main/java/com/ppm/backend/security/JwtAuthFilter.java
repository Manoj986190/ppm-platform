package com.ppm.backend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthFilter implements Filter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        String method = req.getMethod();

        // 🔓 Public APIs
        if (
                (path.equals("/api/users") && method.equals("POST")) || // signup
                path.startsWith("/api/auth")
        ) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 🔐 ROLE CHECK
        String role = jwtUtil.extractRole(token);

        // 🔴 ADMIN ONLY
        if (path.startsWith("/api/users") && !"ROLE_ADMIN".equals(role)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // ✅ USER or ADMIN
        if (path.startsWith("/api/profile")
                && !(role.equals("ROLE_USER") || role.equals("ROLE_ADMIN"))) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
