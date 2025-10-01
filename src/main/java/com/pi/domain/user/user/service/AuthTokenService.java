package com.pi.domain.user.user.service;

import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.util.Ut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {
    @Value("${custom.jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${custom.accessToken.expireSeconds}")
    private int accessTokenExpireSeconds;

    @Value("${custom.refreshToken.expireSeconds}")
    private int refreshTokenExpireSeconds;

    String genAccessToken(User user) {
        long id = user.getId();
        String username = user.getUsername();
        String nickname = user.getNickname();

        Map<String, Object> claims = Map.of("id", id, "username", username, "nickname", nickname);

        return Ut.jwt.toString(
                jwtSecretKey,
                accessTokenExpireSeconds,
                claims
        );
    }

    String genRefreshToken(User user) {
        long id = user.getId();
        String username = user.getUsername();
        String nickname = user.getNickname();

        // Refresh token의 claim은 access token과 동일하지만, 만료 시간이 다릅니다.
        Map<String, Object> claims = Map.of("id", id, "username", username, "nickname", nickname);

        // refreshTokenExpireSeconds는 더 긴 만료 시간을 설정
        return Ut.jwt.toString(
                jwtSecretKey,
                refreshTokenExpireSeconds,
                claims
        );
    }

    public Map<String, Object> payload(String assessToken) {
        Map<String, Object> parsedPayload = Ut.jwt.payload(jwtSecretKey, assessToken);

        if (parsedPayload == null) return null;

        // 값이 Integer든 Long이든 Number로 받아서 longValue() 하면 공통적으로 처리 가능
        long id = ((Number) parsedPayload.get("id")).longValue();

        String username = (String) parsedPayload.get("username");

        String nickname = (String) parsedPayload.get("nickname");

        return Map.of("id", id, "username", username, "nickname", nickname);
    }

    public long getUserIdFromToken(String refreshToken) {
        Map<String, Object> parsedPayload = Ut.jwt.payload(jwtSecretKey, refreshToken);
        if (parsedPayload == null) {
            throw new ServiceException("401-4", "유효하지 않은 토큰입니다.");
        }
        return ((Number) parsedPayload.get("id")).longValue();
    }
}