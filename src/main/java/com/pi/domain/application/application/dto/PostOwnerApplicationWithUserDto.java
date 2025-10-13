package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.user.user.entity.User;

import java.time.LocalDateTime;

public record PostOwnerApplicationWithUserDto(
        long id,
        String status,
        long salary,
        int period,
        LocalDateTime createdDate,
        long userId,
        String userNickname
) {
    public PostOwnerApplicationWithUserDto(Application application, User user) {
        this(
                application.getId(),
                application.getStatus().name(),
                application.getSalary(),
                application.getPeriod(),
                application.getCreatedDate(),
                user.getId(),
                user.getNickname()
        );
    }
}
