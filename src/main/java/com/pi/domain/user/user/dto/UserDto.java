package com.pi.domain.user.user.dto;

import com.pi.domain.user.user.entity.Users;

import java.time.LocalDateTime;

public record UserDto (
        Long id,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String nickname,
        String role
){
    public UserDto(Users user) {
        this(
                user.getId(),
                user.getCreatedDate(),
                user.getModifiedDate(),
                user.getNickname(),
                user.getRole()
        );
    }
}
