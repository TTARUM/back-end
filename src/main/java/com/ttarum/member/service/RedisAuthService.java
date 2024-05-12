package com.ttarum.member.service;

import com.ttarum.member.mail.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisAuthService {

    private final StringRedisTemplate stringTemplate;
    private final RedisTemplate<String, EmailVerification> redisTemplate;

    public String getData(final String key) {
        ValueOperations<String, String> valueOperations = stringTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public EmailVerification getEmailVerification(final String key) {
        ValueOperations<String, EmailVerification> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setData(final String key, final String value) {
        ValueOperations<String, String> valueOperations = stringTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public void setDataExpire(final String key, final String value, final long duration) {
        ValueOperations<String, String> valueOperations = stringTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void setDataExpire(final String key, final EmailVerification value, final long duration) {
        ValueOperations<String, EmailVerification> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(final String key) {
        stringTemplate.delete(key);
    }
}
