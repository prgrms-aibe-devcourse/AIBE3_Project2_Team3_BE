package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;

import java.time.LocalDateTime;

public record ApplicationWriteResBody(
        long id,
        long postId,
        long userId,
        String status,
        String content,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate
) {
    public ApplicationWriteResBody(Application application) {
        this(
                application.getId(),
                application.getPost().getId(),
                application.getUser().getId(),
                application.getStatus().name(),
                application.getContent(),
                application.getCreatedDate(),
                application.getModifiedDate()
        );
    }
}
