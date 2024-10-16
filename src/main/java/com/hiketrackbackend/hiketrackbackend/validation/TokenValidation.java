//package com.hiketrackbackend.hiketrackbackend.validation;
//
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//
//@RequiredArgsConstructor
//public class TokenValidation implements ConstraintValidator<ValidToken, String> {
//    private final RedisTemplate<String, String> redisTemplate;
//
//    @Override
//    public boolean isValid(String token, ConstraintValidatorContext context) {
//        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
//    }
//}
