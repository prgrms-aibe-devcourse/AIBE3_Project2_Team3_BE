package com.pi.domain.user.user.dto;

import com.pi.domain.user.user.entity.User;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String nickname,
        String email,
        String role
) {
    public UserDto(User user) {
        this(
                user.getId(),
                user.getCreatedDate(),
                user.getModifiedDate(),
                user.getNickname(),
                user.getEmail(),
                user.getRole()
        );
    }
    
}
