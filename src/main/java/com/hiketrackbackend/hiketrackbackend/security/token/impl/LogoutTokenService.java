package com.hiketrackbackend.hiketrackbackend.security.token.impl;

import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LogoutTokenService extends UserTokenService<HttpServletRequest> {
    private static final long TIME_TO_LIVE = 900;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String save(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = getUserEmailFromRequest();
            redisTemplate.opsForValue().set(token, email, TIME_TO_LIVE, TimeUnit.SECONDS);
        }
        return token;
    }

    private String getUserEmailFromRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new SecurityException("User id not unauthenticated");
    }
}
