package com.pi.domain.user.user.dto;

import com.pi.domain.user.user.entity.User;

public record UserInviteDto(
        Long userId,
        String userName
) {
    public UserInviteDto(User user) {
        this(user.getId(),
        user.getUsername());
    }
}
