package com.hiketrackbackend.hiketrackbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class TokenService<T> {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public abstract void saveTokenToDB(T param, String email);

    public String getValueByTokenFromDB(String token) {
        String value = redisTemplate.opsForValue().get(token);
        if (value == null) {
            throw new RuntimeException("Value not found with token: " + token);
        }
        return value;
    }

    // TODO может тут можно прокинуть какую то ошибку если не могу обратиться к базе даных впринципе
//    public boolean isTokenExistInDB(String token) {
////        if (token == null) {
////            return true;
////        }
//        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
//    }

    public void deleteTokenInDB(String token) {
        redisTemplate.delete(token);
    }
}
