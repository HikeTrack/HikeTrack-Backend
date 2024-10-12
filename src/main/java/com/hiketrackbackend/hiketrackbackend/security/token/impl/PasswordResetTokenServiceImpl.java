package com.hiketrackbackend.hiketrackbackend.security.token.impl;

import com.hiketrackbackend.hiketrackbackend.model.UserToken;
import com.hiketrackbackend.hiketrackbackend.repository.UserTokenRepository;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements UserTokenService {
    private final UserTokenRepository userTokenRepository;
//    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void createToken(Long userId) {
        UserToken token = new UserToken(
                UUID.randomUUID().toString(),
                userId,
                UserToken.TokenType.PASSWORD_RESET
        );


        userTokenRepository.save(token);
//        redisTemplate.opsForValue().set(token, userEmail, 1, TimeUnit.HOURS);
    }
}
