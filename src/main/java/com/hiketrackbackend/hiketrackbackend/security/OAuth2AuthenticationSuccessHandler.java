package com.hiketrackbackend.hiketrackbackend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String token = jwtUtil.generateToken(authentication.getName());

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
        response.sendRedirect("\"https://hiketrack.github.io/hike-track-frontend/"); //"http://localhost:3000/#
    }
}