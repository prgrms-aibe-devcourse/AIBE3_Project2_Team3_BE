package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;

import java.time.LocalDateTime;

public record ApplicationWriteResBody(
        long id,
        long postId,
        String postType, // "PROJECT" or "FREELANCER"
        long userId,
        String status,
        String content,
        long salary,
        long period,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate
) {
    public ApplicationWriteResBody(Application application) {
        this(
                application.getId(),
                application.getPost().getId(),
                "PROJECT",
                application.getUser().getId(),
                application.getStatus().name(),
                application.getContent(),
                application.getSalary(),
                application.getPeriod(),
                application.getCreatedDate(),
                application.getModifiedDate()
        );
    }
}
