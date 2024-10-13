package com.hiketrackbackend.hiketrackbackend.security.token.impl;

import com.hiketrackbackend.hiketrackbackend.exception.InvalidTokenException;
import com.hiketrackbackend.hiketrackbackend.model.UserToken;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
    Token is a Key due to saving process
*/
@Service
@RequiredArgsConstructor
public class PasswordResetUserTokenServiceImpl implements UserTokenService {
    private final RedisTemplate<String, UserToken> redisTemplate;

    @Override
    public UserToken createToken(Long userId) {
        UserToken userToken = new UserToken();
        userToken.setToken(UUID.randomUUID().toString());
        userToken.setUserId(userId);
        userToken.setTokenType(UserToken.TokenType.PASSWORD_RESET);
        saveToken(userToken);
        return userToken;
    }

    @Override
    public UserToken getUserToken(String tokenKey) {
        UserToken userToken = redisTemplate.opsForValue().get(tokenKey);
        if (userToken == null) {
            throw new InvalidTokenException("Invalid password reset token.");
        }
        return userToken;
    }


    private void saveToken(UserToken userToken) {
        long ttl = userToken.getTokenType().getTimeToLiveInSeconds();
        redisTemplate.opsForValue().set(userToken.getToken(), userToken, ttl, TimeUnit.SECONDS);
    }
}
