package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.post.post.entity.Post;

import java.time.LocalDateTime;

public record ApplicationWithPostDto(
        long id,
        String status,
        long salary,
        int period,
        LocalDateTime createdDate,
        long postId,
        String postType, // "PROJECT" or "FREELANCER"
        String postTitle,
        long postUserId,
        String postUserNickname
) {
    public ApplicationWithPostDto(Application application, Post post) {
        this(
                application.getId(),
                application.getStatus().name(),
                application.getSalary(),
                application.getPeriod(),
                application.getCreatedDate(),
                post.getId(),
                "PROJECT",
                post.getTitle(),
                post.getUser().getId(),
                post.getUser().getNickname()
        );
    }
}
