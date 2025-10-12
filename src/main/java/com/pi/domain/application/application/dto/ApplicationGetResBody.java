package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.file.dto.ApplicationFileDto;

import java.time.LocalDateTime;
import java.util.List;

public record ApplicationGetResBody(
        long id,
        String status,
        String content,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        long postId,
        String postTitle,
        long postUserId,
        String postUserNickname,
        List<ApplicationFileDto> files
) {
    public ApplicationGetResBody(Application application, List<ApplicationFileDto> files) {
        this(
                application.getId(),
                application.getStatus().name(),
                application.getContent(),
                application.getCreatedDate(),
                application.getModifiedDate(),
                application.getPost().getId(),
                application.getPost().getTitle(),
                application.getPost().getUser().getId(),
                application.getPost().getUser().getNickname(),
                files
        );
    }
}
