package com.hiketrackbackend.hiketrackbackend.security;

import com.hiketrackbackend.hiketrackbackend.dto.user.UserForgotRespondDto;
import com.hiketrackbackend.hiketrackbackend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UUIDTokenServiceImpl extends TokenService<String> {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserMapper userMapper;

    // Redis DB save: set(key, value, lifeTime)
    @Override
    public void saveTokenToDB(String token, String userEmail) {
        redisTemplate.opsForValue().set(token, userEmail, 1, TimeUnit.HOURS);
    }

    public UserForgotRespondDto validateResetRequest(String token) {
        boolean isExist = isTokenExistInDB(token);
        if (!isExist) {
            throw new RuntimeException("This link is not valid or expired");
        }
        String email = getValueByTokenFromDB(token);
        deleteTokenInDB(token);
        return userMapper.toDto(email);
    }
}
