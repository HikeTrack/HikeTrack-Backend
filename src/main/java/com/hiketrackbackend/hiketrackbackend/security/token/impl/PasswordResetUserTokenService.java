package com.hiketrackbackend.hiketrackbackend.security.token.impl;

import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetUserTokenService extends UserTokenService<String> {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long TIME_TO_LIVE = 3600;

    @Override
    public String save(String email) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, email, TIME_TO_LIVE, TimeUnit.SECONDS);
        return token;
    }
}
