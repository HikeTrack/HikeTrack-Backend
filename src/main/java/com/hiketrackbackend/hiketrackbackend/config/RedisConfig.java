package com.hiketrackbackend.hiketrackbackend.config;

import com.hiketrackbackend.hiketrackbackend.model.UserToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, String> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new StringRedisSerializer());
//        return template;
//    }

    @Bean
    public RedisTemplate<String, UserToken> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserToken> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Використовуємо Jackson для серіалізації об'єктів
        Jackson2JsonRedisSerializer<UserToken> serializer = new Jackson2JsonRedisSerializer<>(UserToken.class);
        template.setDefaultSerializer(serializer);

        return template;
    }


//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
//        redisConf.setHostName("kind-bison-29263.upstash.io");
//        redisConf.setPort(6379);
//        redisConf.setPassword("AXJPAAIjcDEzMjllYzUwN2I3NjI0ZjliYTE1NWM5YTcwMDZjNjZiZnAxMA");
//
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .useSsl() // Вмикаємо SSL
//                .build();
//
//        return new LettuceConnectionFactory(redisConf, clientConfig);
//    }
}
