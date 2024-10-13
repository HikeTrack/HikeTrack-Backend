package com.hiketrackbackend.hiketrackbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserToken implements Serializable {
    private String token;
    private LocalDateTime createdAt;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Getter
    public enum TokenType {
        PASSWORD_RESET(3600),   // 1 hour after password reset
        CONFIRMATION(10800),    // 3 hours after registration confirmation
        ACCESS(900);            // 15 min after user logout

        private final long timeToLiveInSeconds;

        TokenType(long timeToLiveInSeconds) {
            this.timeToLiveInSeconds = timeToLiveInSeconds;
        }
    }
}
