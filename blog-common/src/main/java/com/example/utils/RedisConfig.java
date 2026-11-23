package com.example.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration 
@ConditionalOnProperty (
    prefix = "spring.data.redis",
    name = "host"
)
public class RedisConfig {
    @Bean 
    Redis redis(StringRedisTemplate stringRedisTemplate) {
        return new Redis(stringRedisTemplate);
    }
}
