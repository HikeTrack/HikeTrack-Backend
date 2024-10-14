package com.hiketrackbackend.hiketrackbackend.security.token;

import com.hiketrackbackend.hiketrackbackend.model.UserToken;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public interface UserTokenService<P> {
    String saveToken(P param);

    UserToken getUserToken(String token);

    default boolean isTokenBlacklisted(String token, RedisTemplate<String, String> redisTemplate) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }


//
//    default void saveToken(UserToken userToken, RedisTemplate<String, UserToken> redisTemplate) {
//        long ttl = userToken.getTokenType().getTimeToLiveInSeconds();
//        redisTemplate.opsForValue().set(userToken.getToken(), userToken, ttl, TimeUnit.SECONDS);
//    }
}
