package com.pi.domain.application.application.dto;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;

import java.time.LocalDateTime;

public record ApplicationWithPostDto(
        long id,
        String status,
        LocalDateTime createdDate,
        long postId,
        String postType, // "PROJECT" or "FREELANCER"
        String postTitle,
        long postUserId,
        String postUserNickname
) {
    public ApplicationWithPostDto(Application application, Post post, User postUser) {
        this(
                application.getId(),
                application.getStatus().name(),
                application.getCreatedDate(),
                post.getId(),
                "PROJECT",
                post.getTitle(),
                postUser.getId(),
                postUser.getNickname()
        );
    }
}
