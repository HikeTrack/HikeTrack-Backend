package com.hiketrackbackend.hiketrackbackend.security.token.impl;

import com.hiketrackbackend.hiketrackbackend.exception.InvalidTokenException;
import com.hiketrackbackend.hiketrackbackend.model.UserToken;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
    Token is a Key due to saving process
*/
@Service
@RequiredArgsConstructor
public class PasswordResetUserTokenServiceImpl implements UserTokenService<String> {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long TIME_TO_LIVE = 3600;

//    @Override
//    public UserToken createToken(String token) {
//        UserToken userToken = new UserToken();
//        userToken.setToken(UUID.randomUUID().toString());
//        userToken.setUserId(userId);
//        userToken.setCreatedAt(LocalDateTime.now());
//        userToken.setTokenType(UserToken.TokenType.PASSWORD_RESET);
//        saveToken(userToken, redisTemplate);
//        return userToken;
//    }
//
    @Override
    public UserToken getUserToken(String token) {
        String email = redisTemplate.opsForValue().get(token);
        if (userToken == null) {
            throw new InvalidTokenException("Invalid password reset token.");
        }
        return userToken;
    }


    @Override
    public String saveToken(String email) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, email, TIME_TO_LIVE, TimeUnit.SECONDS);
        return token;
    }
}
