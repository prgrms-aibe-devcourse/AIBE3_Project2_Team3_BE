package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;

import java.time.LocalDateTime;

public record PostOwnerApplicationWithUserDto(
        long id,
        String status,
        LocalDateTime createdDate,
        long userId,
        String userNickname
) {
    public PostOwnerApplicationWithUserDto(Application application) {
        this(
                application.getId(),
                application.getStatus().name(),
                application.getCreatedDate(),
                application.getUser().getId(),
                application.getUser().getNickname()
        );
    }
}
