package com.hiketrackbackend.hiketrackbackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UUIDTokenServiceImpl extends TokenService<String> {
    private final RedisTemplate<String, String> redisTemplate;

    // Redis DB save: set(key, value, lifeTime)
    @Override
    public void saveToken(String token, String userEmail) {
        redisTemplate.opsForValue().set(token, userEmail, 1, TimeUnit.HOURS);
    }
}
