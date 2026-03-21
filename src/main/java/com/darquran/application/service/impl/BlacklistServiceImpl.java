package com.darquran.application.service.impl;

import com.darquran.application.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void blackListToken(String token, long timeToLive) {

        redisTemplate.opsForValue().set(token, "true", timeToLive, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}

