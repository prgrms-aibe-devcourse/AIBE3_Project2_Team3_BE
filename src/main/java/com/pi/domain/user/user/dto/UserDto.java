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

    // 추후 추가로직 필요시 작성
    public static UserDto from(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user);
    }
}
