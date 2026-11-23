package com.example.utils;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class Redis {
    private static final String REDIS_PREFIX_KEY = "default_prefix";
    private static final String REDIS_KEY_SPLIT = ":";
    private StringRedisTemplate redisTemplate;

    public Redis(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("redis get error, key: {}, e: ", key, e);
            return null;
        }
    }

    public boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis set error, key: {}, value: {}, e: ", key, value, e);
            return false;
        }
    }

    public boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, Duration.ofMillis(time));
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("redis set error, key: {}, value: {}, e: ", key, value, e);
            return false;
        }
    }

    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("redis haskey erorr, key: {}, e: ", key, e);
            return false;
        }
    }

    public static String keyBuilder(String prefix, String... args) {
        if (prefix == null) {
            prefix = REDIS_PREFIX_KEY;
        }
        StringBuilder key = new StringBuilder(prefix);
        for (String arg: args) {
            key.append(REDIS_KEY_SPLIT).append(arg);
        }
        return key.toString();
    }
}
