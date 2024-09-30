package com.hiketrackbackend.hiketrackbackend.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl extends TokenService<HttpServletRequest> {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_NAME = "Bearer ";


    @Override
    public void saveToken(HttpServletRequest request, String userEmail) {
        String token = getToken(request);
        if (token == null) {
            throw new RuntimeException("Missing token in authorization header");
        }
        redisTemplate.opsForValue().set(token, userEmail, 20, TimeUnit.MINUTES);
    }

    protected String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_NAME)) {
            return bearerToken.substring(TOKEN_NAME.length());
        }
        return null;
    }
}
