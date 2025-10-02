package com.pi.domain.user.user.service;

import com.pi.domain.user.user.entity.RefreshToken;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.RefreshTokenRepository;
import com.pi.global.exception.ServiceException;
import com.pi.global.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock = Clock.systemUTC();

    @Value("${custom.jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${custom.accessToken.expireSeconds}")
    private int accessTokenExpireSeconds;

    @Value("${custom.refreshToken.expireSeconds}")
    private int refreshTokenExpireSeconds;

    /* =================== 액세스 토큰 (JWT) =================== */
    public String genAccessToken(User user) {
        Map<String, Object> claims = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname(),
                // ★ 필터에서 role을 읽으므로 포함
                "role", user.getRole()
        );
        return Ut.jwt.toString(jwtSecretKey, accessTokenExpireSeconds, claims);
    }

    /** 액세스 토큰 검증/파싱 */
    public Map<String, Object> payload(String accessToken) {
        Map<String, Object> parsed = Ut.jwt.payload(jwtSecretKey, accessToken);
        if (parsed == null) return null;
        long id = ((Number) parsed.get("id")).longValue();
        return Map.of(
                "id", id,
                "username", (String) parsed.get("username"),
                "nickname", (String) parsed.get("nickname"),
                "role", (String) parsed.getOrDefault("role", "ROLE_USER")
        );
    }

    /* =================== 리프레시 토큰 (불투명) =================== */

    @Transactional
    public String issueRefresh(User user) {
        String plain = Ut.jwt.newOpaqueToken(64);
        String hash  = Ut.jwt.sha256(plain);
        Instant exp  = Instant.now(clock).plus(Duration.ofSeconds(refreshTokenExpireSeconds));
        refreshTokenRepository.save(RefreshToken.of(user, hash, exp));
        return plain; // HttpOnly 쿠키로 내려갈 원문
    }

    /** ★ 활성 리프레시의 '소유자' 조회 (fetch join으로 User 초기화) */
    @Transactional(readOnly = true)
    public User findActiveRefreshOwner(String refreshPlain) {
        if (refreshPlain == null || refreshPlain.isBlank())
            throw new ServiceException("401-3", "refreshToken이 비어있습니다.");

        String hash = Ut.jwt.sha256(refreshPlain);
        RefreshToken rt = refreshTokenRepository.findActiveWithUserByTokenHash(hash)
                .orElseThrow(() -> new ServiceException("401-1", "유효하지 않은 Token 입니다."));

        if (!rt.isActive(Instant.now(clock)))
            throw new ServiceException("401-2", "Token이 만료되었습니다.");

        return rt.getUser(); // 이미 초기화된 User
    }

    /** 회전: 이전 토큰 revoke 후 새 리프레시 발급 */
    @Transactional
    public String rotateRefresh(String refreshPlain) {
        String hash = Ut.jwt.sha256(refreshPlain);
        RefreshToken rt = refreshTokenRepository.findByTokenHashAndRevokedFalse(hash)
                .orElseThrow(() -> new ServiceException("401-1", "유효하지 않은 Token 입니다."));
        if (!rt.isActive(Instant.now(clock)))
            throw new ServiceException("401-2", "Token이 만료되었습니다.");

        rt.revoke();

        String newPlain = Ut.jwt.newOpaqueToken(64);
        String newHash  = Ut.jwt.sha256(newPlain);
        Instant exp     = Instant.now(clock).plus(Duration.ofSeconds(refreshTokenExpireSeconds));
        refreshTokenRepository.save(RefreshToken.of(rt.getUser(), newHash, exp));
        return newPlain;
    }

    @Transactional
    public void revokeRefresh(String refreshPlain) {
        String hash = Ut.jwt.sha256(refreshPlain);
        refreshTokenRepository.findByTokenHashAndRevokedFalse(hash)
                .ifPresent(RefreshToken::revoke);
    }
}
