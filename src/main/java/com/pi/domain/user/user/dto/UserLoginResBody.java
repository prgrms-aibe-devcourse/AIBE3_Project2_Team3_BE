package com.pi.domain.user.user.dto;

public record UserLoginResBody (
        UserDto item,
        String accessToken,
        String refreshToken
) {
}
