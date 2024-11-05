package com.hiketrackbackend.hiketrackbackend.security;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final List<String> EXCLUDE_URLS = Arrays.asList("/auth/**", "/oauth2/**");
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserTokenService<HttpServletRequest> logoutTokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtUtil.getToken(request);
        if (EXCLUDE_URLS.stream().anyMatch(exclude -> path.startsWith(exclude))) {
            filterChain.doFilter(request, response);
            return;
        }
        if (token != null && jwtUtil.isValidToken(token)) {
            if (logoutTokenService.isKeyExist(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Login first please");
                return;
            }
            String username = jwtUtil.getUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
