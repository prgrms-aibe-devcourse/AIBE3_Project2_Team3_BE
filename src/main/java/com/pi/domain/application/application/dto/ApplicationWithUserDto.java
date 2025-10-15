package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;

import java.time.LocalDateTime;

public record ApplicationWithUserDto(
        long id,
        String status,
        long salary,
        int period,
        LocalDateTime createdDate,
        long postId,
        String postType, // "PROJECT" or "FREELANCER"
        String postTitle,
        long userId,
        String userNickname
) {
    public ApplicationWithUserDto(Application application, Post post, User user) {
        this(
                application.getId(),
                application.getStatus().name(),
                application.getSalary(),
                application.getPeriod(),
                application.getCreatedDate(),
                post.getId(),
                "PROJECT",
                post.getTitle(),
                user.getId(),
                user.getNickname()
        );
    }
}