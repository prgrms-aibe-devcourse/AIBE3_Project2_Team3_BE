package com.pi.domain.user.user.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RefreshTokenStore {
    private final StringRedisTemplate redis;

    private String keyRefresh(String jti) { return "refresh:" + jti; }
    private String keyUserSessions(long userId) { return "user:sessions:" + userId; }
    private String keyAuthVer(long userId) { return "authver:" + userId; }

    // 저장 (TTL 부여)
    public void saveRefresh(String jti, long userId, Duration ttl, String payloadJson) {
        redis.opsForValue().set(keyRefresh(jti), payloadJson, ttl);
        redis.opsForSet().add(keyUserSessions(userId), jti);
    }

    public @Nullable String findRefreshPayload(String jti) {
        return redis.opsForValue().get(keyRefresh(jti));
    }

    public void deleteRefresh(String jti, long userId) {
        redis.delete(keyRefresh(jti));
        redis.opsForSet().remove(keyUserSessions(userId), jti);
    }

    public void revokeAllForUser(long userId) {
        String k = keyUserSessions(userId);
        Set<String> all = redis.opsForSet().members(k);
        if (all != null) {
            for (String jti : all) redis.delete(keyRefresh(jti));
            redis.delete(k);
        }
    }

    // 권한 버전
    public long getAuthVersion(long userId) {
        String v = redis.opsForValue().get(keyAuthVer(userId));
        return (v == null) ? 1L : Long.parseLong(v);
    }

    public void bumpAuthVersion(long userId) {
        redis.opsForValue().increment(keyAuthVer(userId)); // 없다면 1 생성
    }
}
