package com.xdpsx.music.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.xdpsx.music.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import java.io.IOException;
import java.util.Set;

@Service
public class RedisService implements CacheService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisObjectMapper = createObjectMapper();
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Override
    public void setValue(String key, Object value) {
        try {
            String json = redisObjectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
            logger.error("Error", e);
        }
    }
    @Override
    public void setValue(String key, Object value, int durationMs) {
        try {
            String json = redisObjectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, Duration.ofMillis(durationMs));
        } catch (JsonProcessingException e) {
            logger.error("Error", e);
        }
    }

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


    public <T> T getValue(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        try {
            return json != null ? redisObjectMapper.readValue(json, new TypeReference<>() {}) : null;
        } catch (IOException e) {
//            throw new RuntimeException("Failed to deserialize value from Redis", e);
            logger.error("Failed to deserialize value from Redis", e);
            return null;
        }
    }

    @Override
    public void deleteKeysByPrefix(String prefix) {
        String pattern = prefix + "*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
