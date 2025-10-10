package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;

import java.time.LocalDateTime;

public record ApplicationGetResBody(
        long id,
        String status,
        String content,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        long postId,
        String postTitle,
//        String postStatus,
        long postUserId,
        String postUserNickname
) {
    public ApplicationGetResBody(Application application) {
        this(
                application.getId(),
                application.getStatus().name(),
                application.getContent(),
                application.getCreatedDate(),
                application.getModifiedDate(),
                application.getPost().getId(),
                application.getPost().getTitle(),
//                application.getPost().getStatus().name(),
                application.getPost().getUser().getId(),
                application.getPost().getUser().getNickname()
        );
    }
}
