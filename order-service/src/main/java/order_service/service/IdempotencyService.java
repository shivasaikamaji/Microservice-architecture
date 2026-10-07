package order_service.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

    private static final Duration TTL = Duration.ofHours(24);
    private static final String PREFIX = "idempotency:order:";

    private final StringRedisTemplate redis;

    public IdempotencyService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private String redisKey(Long userId, String key) {
        return PREFIX + userId + ":" + key;
    }

    // true = first time we see this key, false = duplicate
    public boolean startProcessing(Long userId, String key) {
        String rk = redisKey(userId, key);
        Boolean first = redis.opsForHash().putIfAbsent(rk, "status", "PROCESSING");
        if (Boolean.TRUE.equals(first)) {
            redis.opsForHash().put(rk, "idempotencyKey", key);
            redis.opsForHash().put(rk, "userId", String.valueOf(userId));
            redis.opsForHash().put(rk, "createdAt", LocalDateTime.now().toString());
            redis.expire(rk, TTL);
            return true;
        }
        return false;
    }

    // save the result (the order id) and mark the request as finished
    public void markCompleted(Long userId, String key, Long orderId) {
        String rk = redisKey(userId, key);
        redis.opsForHash().put(rk, "orderId", String.valueOf(orderId));
        redis.opsForHash().put(rk, "status", "COMPLETED");
    }

    public void remove(Long userId, String key) {
        redis.delete(redisKey(userId, key));
    }

    public String getStatus(Long userId, String key) {
        Object v = redis.opsForHash().get(redisKey(userId, key), "status");
        return v == null ? null : v.toString();
    }

    public Long getOrderId(Long userId, String key) {
        Object v = redis.opsForHash().get(redisKey(userId, key), "orderId");
        return v == null ? null : Long.valueOf(v.toString());
    }
}