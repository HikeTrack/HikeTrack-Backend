package com.hiketrackbackend.hiketrackbackend.config;

import com.hiketrackbackend.hiketrackbackend.model.UserToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, UserToken> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserToken> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<UserToken> serializer = new Jackson2JsonRedisSerializer<>(UserToken.class);
        template.setDefaultSerializer(serializer);

        return template;
    }
}
