package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;

import java.time.LocalDateTime;

public record PostOwnerApplicationGetResBody(
        long id,
        String status,
        String content,
        LocalDateTime createdDate,
        long userId,
        String userNickname
) {
    public PostOwnerApplicationGetResBody(Application application) {
        this(
                application.getId(),
                application.getStatus().name(),
                application.getContent(),
                application.getCreatedDate(),
                application.getUser().getId(),
                application.getUser().getNickname()
        );
    }
}
