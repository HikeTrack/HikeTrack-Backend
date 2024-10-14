package com.hiketrackbackend.hiketrackbackend.security.token.impl;

import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.UserToken;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.security.token.UserTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogoutTokenServiceImpl implements UserTokenService<HttpServletRequest> {
    private final RedisTemplate<String, UserToken> redisTemplate;
    private final UserRepository userRepository;

    @Override
    public UserToken createToken(HttpServletRequest request) {
        UserToken userToken = new UserToken();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        userToken.setToken(token);
        userToken.setUserId(getUserId());
        userToken.setCreatedAt(LocalDateTime.now());
        userToken.setTokenType(UserToken.TokenType.ACCESS);
        saveToken(userToken, redisTemplate);
        return userToken;
    }

    @Override
    public UserToken getUserToken(String tokenKey) {
        return;
    }

    private Long getUserId() {
        String email = getUserEmailFromRequest();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
        return user.getId();
    }

    private String getUserEmailFromRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new SecurityException("User id not unauthenticated");
    }
}
