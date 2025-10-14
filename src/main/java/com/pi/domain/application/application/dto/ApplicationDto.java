package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.file.dto.ApplicationFileDto;

import java.time.LocalDateTime;
import java.util.List;

public record ApplicationDto(
        long id,
        long postId,
        String postType, // "PROJECT" or "FREELANCER"
        long userId,
        String status,
        String content,
        long salary,
        int period,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        List<ApplicationFileDto> files
) {
    public ApplicationDto(Application application, List<ApplicationFileDto> files) {
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
                application.getModifiedDate(),
                files
        );
    }
}
