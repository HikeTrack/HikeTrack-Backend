package com.hiketrackbackend.hiketrackbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class TokenService<T> {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public abstract void saveToken(T param, String email);

    public String getValueByToken(String token) {
        String value = redisTemplate.opsForValue().get(token);
        if (value == null) {
            throw new RuntimeException("Value not found with token: " + token);
        }
        return value;
    }

    public boolean isTokenExist(String token) {
        if (token == null) {
            return true;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }
}
