package com.pi.domain.user.user.service;

import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthTokenService {
    private final RefreshTokenStore rtStore;
    private final UserService userService; // 권한 변경 시 authVersion 올릴 때 사용
    private final Clock clock = Clock.systemUTC();

    @Value("${custom.jwt.secretKey}")
    private String jwtSecretKey;
    @Value("${custom.accessToken.expireSeconds}")
    private int accessTokenExpireSeconds;
    @Value("${custom.refreshToken.expireSeconds}")
    private int refreshTokenExpireSeconds;

    /* =================== 액세스 토큰 (JWT) =================== */
    public String genAccessToken(User user) {
        long authVersion = rtStore.getAuthVersion(user.getId());
        Map<String, Object> claims = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname(),
                "authVersion", authVersion,
                "roles", user.getAuthoritiesStringList()
        );

        return Ut.jwt.toString(jwtSecretKey, accessTokenExpireSeconds, claims);
    }

    /** AT 파싱 */
    public Map<String, Object> payload(String accessToken) {
        return Ut.jwt.payload(jwtSecretKey, accessToken); // {id, username, nickname, authVersion, roles}
    }

    /* ===== Refresh Token (opaque) in Redis ===== */
    public String issueRefresh(User user) {
        String jti = Ut.jwt.newOpaqueToken(64); // 원문(쿠키)
        Instant exp = Instant.now(clock).plusSeconds(refreshTokenExpireSeconds);
        String payloadJson = Ut.json.toString(Map.of(
                "userId", user.getId(),
                "exp", exp.getEpochSecond()
        ));
        rtStore.saveRefresh(jti, user.getId(), Duration.ofSeconds(refreshTokenExpireSeconds), payloadJson);
        return jti;
    }

    /** 회전 */
    public String rotateRefresh(String oldJti) {
        String payload = rtStore.findRefreshPayload(oldJti);
        if (payload == null) throw new ServiceException("401-1", "유효하지 않은 Token 입니다.");

        long userId = ((Number) Ut.json.parse(payload, Map.class).get("userId")).longValue();
        // old 삭제
        rtStore.deleteRefresh(oldJti, userId);

        // new 발급
        String newJti = Ut.jwt.newOpaqueToken(64);
        Instant exp = Instant.now(clock).plusSeconds(refreshTokenExpireSeconds);
        String newPayload = Ut.json.toString(Map.of("userId", userId, "exp", exp.getEpochSecond()));
        rtStore.saveRefresh(newJti, userId, Duration.ofSeconds(refreshTokenExpireSeconds), newPayload);
        return newJti;
    }

    public long findRefreshOwner(String jti) {
        String payload = rtStore.findRefreshPayload(jti);
        if (payload == null) throw new ServiceException("401-1", "유효하지 않은 Token 입니다.");
        return ((Number) Ut.json.parse(payload, Map.class).get("userId")).longValue();
    }

    public void revokeRefresh(String jti) {
        String payload = rtStore.findRefreshPayload(jti);
        if (payload == null) return;
        long userId = ((Number) Ut.json.parse(payload, Map.class).get("userId")).longValue();
        rtStore.deleteRefresh(jti, userId);
    }

    public void revokeAll(long userId) {
        rtStore.revokeAllForUser(userId);
    }

    /* 권한 변경 시 호출 */
    public void bumpAuthVersion(long userId) {
        rtStore.bumpAuthVersion(userId);
    }
}
