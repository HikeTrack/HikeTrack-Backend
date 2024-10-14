package com.hiketrackbackend.hiketrackbackend.security.token;

import com.hiketrackbackend.hiketrackbackend.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class UserTokenService<P> {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public abstract String save(P param);

    public String getValue(String token) {
        if (!isKeyExist(token)) {
            throw new InvalidTokenException("Invalid password reset token: " + token);
        }
        return redisTemplate.opsForValue().get(token);
    }

    public boolean isKeyExist(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}

