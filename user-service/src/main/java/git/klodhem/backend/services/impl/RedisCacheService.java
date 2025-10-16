package git.klodhem.backend.services.impl;

import git.klodhem.backend.services.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisCacheService implements CacheService {

    private final RedisTemplate<String, String> redisTemplate;

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofHours(1));
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
