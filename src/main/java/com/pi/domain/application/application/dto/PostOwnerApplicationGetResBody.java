package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.application.file.dto.ApplicationFileDto;
import com.pi.domain.user.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record PostOwnerApplicationGetResBody(
        long id,
        String status,
        String content,
        long salary,
        int period,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        List<ApplicationFileDto> files,
        long userId,
        String userNickname
) {
    public PostOwnerApplicationGetResBody(Application application, User user, List<ApplicationFileDto> files) {
        this(
                application.getId(),
                application.getStatus().name(),
                application.getContent(),
                application.getSalary(),
                application.getPeriod(),
                application.getCreatedDate(),
                application.getModifiedDate(),
                files,
                application.getUser().getId(),
                application.getUser().getNickname()
        );
    }
}
