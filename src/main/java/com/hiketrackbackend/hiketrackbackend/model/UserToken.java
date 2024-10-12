package com.hiketrackbackend.hiketrackbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@RedisHash
public class UserToken {
    @Id
    private String id;
    private String token;
    private LocalDateTime createdAt;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    public UserToken() {}

    public UserToken(String token, Long userId, TokenType type) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public enum TokenType {
        CONFIRMATION,
        PASSWORD_RESET,
        LOGOUT
    }
}
